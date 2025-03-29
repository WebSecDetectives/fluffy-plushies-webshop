# routes/products.rb
require "sinatra"
require "json"
require_relative "../models/product"

# GET /products - list all
get "/products" do
  content_type :json
  Product.all.to_json
end

# POST /products - create one
post "/products" do
  content_type :json
  data = JSON.parse(request.body.read)

  result = Product.create(data)
  status 201
  result.to_json
end
