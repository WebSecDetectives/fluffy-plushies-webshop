require 'bunny'
require 'dotenv/load'

module MessageBus
  def self.channel
    @connection ||= Bunny.new(ENV['RABBITMQ_URL'] || 'amqp://guest:guest@localhost')
    @connection.start unless @connection.open?
    @channel ||= @connection.create_channel
  end

  def self.publish(exchange_name, routing_key, message)
    exchange = channel.topic(exchange_name, durable: true)
    exchange.publish(message.to_json, routing_key: routing_key)
  end
end
