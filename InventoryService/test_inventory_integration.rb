require 'bunny'
require 'json'
require 'securerandom'

connection = Bunny.new
connection.start
channel = connection.create_channel

# Declare both queues
request_queue = channel.queue('inventory.reservation_requests', durable: true)
response_queue = channel.queue('inventory.reservation_responses', durable: true)

# Generate a unique correlation ID
correlation_id = SecureRandom.uuid

# Listen to response queue
puts "👂 Waiting for response with correlation_id: #{correlation_id}"
response_queue.subscribe(block: false) do |delivery_info, properties, payload|
  if properties.correlation_id == correlation_id
    puts "\n✅ Got response from InventoryService:\n#{payload}"
    exit 0
  else
    puts "⚠️ Ignored unrelated message"
  end
end

# Simulate a reservation request message
test_message = {
  items: [
    { item_id: "1", quantity: 1 },
    { item_id: "2", quantity: 2 }
  ]
}

# Publish the test message
channel.default_exchange.publish(
  test_message.to_json,
  routing_key: request_queue.name,
  correlation_id: correlation_id
)

puts "📤 Sent reservation request:\n#{test_message.to_json}"
