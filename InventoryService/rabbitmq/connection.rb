require 'bunny'

module RabbitMQ
  def self.channel
    @connection ||= Bunny.new(ENV['RABBITMQ_URL'] || 'amqp://guest:guest@rabbitmq')
    @connection.start unless @connection.open?

    @channel ||= @connection.create_channel
  end
end
