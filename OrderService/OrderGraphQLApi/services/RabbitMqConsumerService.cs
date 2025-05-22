using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using Microsoft.Extensions.Logging;

namespace OrderGraphQLApi.services
{
    public class RabbitMqConsumerService : BackgroundService
    {
        private readonly IConnection _connection;
        private readonly IModel _channel;
        private readonly ILogger<RabbitMqConsumerService> _logger;
        private const string ExchangeName = "order_exchange";
        private const string QueueName = "order.confirmed";

    

        public RabbitMqConsumerService(IConnection connection, ILogger<RabbitMqConsumerService> logger)
        {
            _connection = connection;
            _channel = _connection.CreateModel();
            _logger = logger;

            // Declare exchange & queue, bind queue
            _channel.ExchangeDeclare(ExchangeName, "direct", durable: true);

            _channel.QueueDeclare(queue: QueueName,
                                  durable: true,
                                  exclusive: false,
                                  autoDelete: false,
                                  arguments: null);

            _channel.QueueBind(queue: QueueName,
                               exchange: ExchangeName,
                               routingKey: QueueName);
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            stoppingToken.ThrowIfCancellationRequested();

            var consumer = new AsyncEventingBasicConsumer(_channel);

            consumer.Received += async (model, ea) =>
            {
                var body = ea.Body.ToArray();
                var message = Encoding.UTF8.GetString(body);

             //   _logger.LogInformation($"[RabbitMqConsumerService] Received message from {QueueName}: {message}");

                // TODO: Do your message processing here
               // Console.WriteLine($"[RabbitMqConsumerService] Received message from {QueueName}: {message}");
                // Acknowledge the message
                _channel.BasicAck(ea.DeliveryTag, multiple: false);

                await Task.Yield();
            };

            _channel.BasicConsume(queue: QueueName,
                                  autoAck: false,
                                  consumer: consumer);

            return Task.CompletedTask;
        }

        public override void Dispose()
        {
            _channel?.Close();
            _connection?.Close();
            base.Dispose();
        }
    }
}