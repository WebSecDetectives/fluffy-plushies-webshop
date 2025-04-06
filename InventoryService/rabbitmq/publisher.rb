require_relative 'connection'
require 'json'

module RabbitMQ
  class Publisher
    def self.publish(queue, message)
      q = RabbitMQ.channel.queue(queue, durable: true)
      q.publish(message.to_json, persistent: true)
    end
  end
end
