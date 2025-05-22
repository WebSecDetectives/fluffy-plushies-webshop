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

# Set CORS headers for all routes
before do
  response.headers['Access-Control-Allow-Origin'] = '*'
  response.headers['Access-Control-Allow-Methods'] = 'GET, POST, PUT, DELETE, OPTIONS'
  response.headers['Access-Control-Allow-Headers'] = 'Origin, Content-Type, Accept, Authorization, Token'
end

# Handle preflight requests
options '*' do
  200
end

set :bind, '0.0.0.0'

configure do
  # Declare only relevant queue
  RabbitMQ.channel.queue('inventory_reservation_responses', durable: true)
end

begin
  # Test DB connection
  DB.test_connection
  puts "✅ Successfully connected to the database!"
rescue Sequel::DatabaseConnectionError => e
  puts "❌ Failed to connect to the database: #{e.message}"
end

# Start consumer thread
Thread.new do
  begin
    puts "🟢 Inventory consumer started. Waiting for messages in 'inventory_reservation_requests' queue..."
    queue = RabbitMQ.channel.queue('inventory_reservation_requests', durable: true)
    queue.subscribe(block: false) do |delivery_info, properties, payload|
      puts "📥 Received message in 'inventory_reservation_requests': #{payload}"
      data = JSON.parse(payload)
      line_items = data['line_items']

      # First, check if all items can be fulfilled
      all_available = line_items.all? do |item|
        product = Product[item['item_id']]
        product && product.stock >= item['quantity']
      end

      if all_available
        # All items are available, now update stock
        line_items.each do |item|
          product = Product[item['item_id']]
          product.update(stock: product.stock - item['quantity'])
          product.save
        end
        RabbitMQ::Publisher.publish(
          { line_items: line_items },
          correlation_id: properties.correlation_id
        )
      else
        # At least one item is not available, do not update any stock
        RabbitMQ::Publisher.publish(
          { error_code: 404, message: 'Not enough stock', line_items: line_items },
          correlation_id: properties.correlation_id
        )
      end
    end
  rescue => e
    puts "❌ Inventory consumer error: #{e.message}"
  end
end
