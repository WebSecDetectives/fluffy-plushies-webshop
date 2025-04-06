require 'sinatra'
require 'sinatra/json'
require 'json'
require 'dotenv/load'
require_relative 'routes/products'
require_relative 'rabbitmq/connection'
require_relative 'rabbitmq/publisher'
require 'bunny'

def publish_product_created_message(product)
  connection = Bunny.new(hostname: ENV['RABBITMQ_HOST'], username: ENV['RABBITMQ_USER'], password: ENV['RABBITMQ_PASS'])
  connection.start

  channel = connection.create_channel
  queue = channel.queue('product.created', durable: true)

  message = {
    id: product[:id],
    name: product[:name],
    price: product[:price],
    stock: product[:stock]
  }.to_json

  queue.publish(message, persistent: true)
  puts "📦 Sent message to RabbitMQ: #{message}"

  connection.close
end

configure do
  # Make sure the queue is declared
  RabbitMQ.channel.queue('product_events', durable: true)
end
set :bind, '0.0.0.0'
