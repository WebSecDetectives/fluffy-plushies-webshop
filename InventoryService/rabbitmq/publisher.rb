require 'bunny'
require 'json'

module RabbitMQ
  class Publisher
    def self.publish(payload, correlation_id: nil)
      connection = Bunny.new(ENV['RABBITMQ_URL'] || 'amqp://guest:guest@rabbitmq')
      connection.start

      channel = connection.create_channel
      queue_name = 'inventory_reservation_responses'
      queue = channel.queue(queue_name, durable: true)

      options = {
        routing_key: queue.name,
        persistent: true
      }
      options[:correlation_id] = correlation_id if correlation_id

      channel.default_exchange.publish(
        payload.to_json,
        options
      )

      puts "[RabbitMQ] Published to #{queue.name}: #{payload}"
      connection.close
    end
  end
end
