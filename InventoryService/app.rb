
require 'sinatra'
require 'sinatra/json'
require 'json'
require 'dotenv/load'
require_relative 'routes/products'
require_relative 'rabbitmq/connection'
require_relative 'config/database'
require_relative 'rabbitmq/publisher'
require 'bunny'
require_relative 'config/elasticsearch'


set :bind, '0.0.0.0'

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
  RabbitMQ.channel.queue('indexer', durable: true)
end

begin
  # Try a simple query to confirm the connection works
  DB.test_connection
  puts "✅ Successfully connected to the database!"
rescue Sequel::DatabaseConnectionError => e
  puts "❌ Failed to connect to the database: #{e.message}"
end

# Declare the queue (make sure it's consistent with the consumer)
queue = RabbitMQ.channel.queue('indexer')

# Create a test message
message = {
  event: 'test_index',
  payload: {
    id: 'abc123',
    content: 'Hello from the publisher!'
  }
}
 
# Publish it
RabbitMQ.channel.default_exchange.publish(message.to_json, routing_key: queue.name)