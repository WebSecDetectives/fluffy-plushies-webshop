# routes/products.rb
require 'sinatra'
require 'json'
require_relative '../models/product'

get '/products' do
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
    product.to_json
  else
    halt 422, { errors: product.errors.full_messages }.to_json
  end
end

delete '/products/:id' do
  product = Product[params[:id]]
  halt 404, { error: 'Product not found' }.to_json unless product
  product.delete
  status 204
end

require_relative '../rabbitmq/publisher'

# Inside your POST or PUT routes, after saving/updating:
RabbitMQ::Publisher.publish('product_events', {
  event: 'product_created', # or 'product_updated'
  product: product.values
})

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
