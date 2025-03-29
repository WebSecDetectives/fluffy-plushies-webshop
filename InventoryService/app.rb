# app.rb
require "sinatra"
require_relative "./routes/products"

set :bind, "0.0.0.0"

get "/" do
  "Inventory service is live!"
end
