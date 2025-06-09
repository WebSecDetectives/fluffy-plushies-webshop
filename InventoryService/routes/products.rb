# routes/products.rb
require 'sinatra'
require 'json'
require_relative '../models/product'

get '/products' do
  content_type :json
  products = Product.all
  json products.map(&:values)
end


get '/products/:id' do
  content_type :json
  product = Product[params[:id]]
  halt 404, { error: 'Product not found' }.to_json unless product
  product.to_json
end

post '/products' do
  content_type :json
  data = JSON.parse(request.body.read)

  product = Product.new(
    name:        data['name'],
    price:       data['price'],
    description: data['description'],
    stock:       data['stock']
  )

  if product.valid?
    product.save
    RabbitMQ::Publisher.publish('indexer', {
      event: 'product_created',
      product: product.values
    })
    status 201
    product.to_json
  else
    halt 422, { errors: product.errors.full_messages }.to_json
  end
end

put '/products/:id' do
  content_type :json
  product = Product[params[:id]]
  halt 404, { error: 'Product not found' }.to_json unless product

  data = JSON.parse(request.body.read)
  product.set_fields(data, %w[name price description stock], missing: :skip)

  if product.valid?
    product.save
    RabbitMQ::Publisher.publish('indexer', {
      event: 'product_updated',
      product: product.values
    })
    product.to_json
  else
    halt 422, { errors: product.errors.full_messages }.to_json
  end
end

delete '/products/:id' do
  product = Product[params[:id]]
  halt 404, { error: 'Product not found' }.to_json unless product
  product.delete
  RabbitMQ::Publisher.publish('indexer', {
    event: 'product_deleted',
    productid: product.values.id
  })
  status 204
end

require_relative '../rabbitmq/publisher'

get '/test-publish' do
  product = {
    id: 1,
    name: "Flushy the Bear",
    price: 19.99,
    stock: 100
  }

  publish_product_created_message(product)
  json message: 'Message sent!'
end

get '/products/search' do
  query = params[:query]
  halt 400, json({ error: "Missing query param" }) unless query

  results = Product.where(Sequel.ilike(:name, "%#{query}%")).all
  json results.map(&:values)
end

get '/products/search/:term' do
  term = params[:term]
  results = ES_CLIENT.search(index: 'products', body: {
    query: {
      multi_match: {
        query: term,
        fields: ['name^3', 'description']
      }
    }
  })

  hits = results['hits']['hits'].map { |hit| hit['_source'] }
  json hits
end
