using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;

namespace OrderGraphQLApi.services
{
    public class UserInformationResponsesConsumer : BackgroundService
    {
        private readonly IConnection _connection;
        private readonly IModel _channel;
        private readonly ILogger<UserInformationResponsesConsumer> _logger;
        private readonly IServiceScopeFactory _serviceScopeFactory;

        private const string ExchangeName = "order_exchange";
        private const string QueueName = "identity.user_information_responses";

        public UserInformationResponsesConsumer(
            IConnection connection,
            ILogger<UserInformationResponsesConsumer> logger,
            IServiceScopeFactory serviceScopeFactory)
        {
            _connection = connection;
            _channel = _connection.CreateModel();
            _logger = logger;
            _serviceScopeFactory = serviceScopeFactory;

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
        var consumer = new AsyncEventingBasicConsumer(_channel);

        consumer.Received += async (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var message = Encoding.UTF8.GetString(body);
            var correlationId = ea.BasicProperties?.CorrelationId;

            // Check for correlation_id in headers if CorrelationId is not set
            if (string.IsNullOrEmpty(correlationId) && ea.BasicProperties?.Headers != null &&
                ea.BasicProperties.Headers.TryGetValue("correlation_id", out var headerValue))
            {
                correlationId = Encoding.UTF8.GetString((byte[])headerValue);
            }

            var messageId = ea.BasicProperties?.MessageId;

            if (messageId == "user_information_response_error")
            {
                _logger.LogError($"[UserInformationResponsesConsumer] Received message from {QueueName}: {message}");
                return;
            }

            using (var scope = _serviceScopeFactory.CreateScope())
            {
                var orderService = scope.ServiceProvider.GetRequiredService<OrderService>();

                try
                {
                    await orderService.getLineItemsByOrderId(message, correlationId);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error handling message.");
                }
            }

            _channel.BasicAck(ea.DeliveryTag, multiple: false);
        };

        _channel.BasicConsume(queue: QueueName, autoAck: false, consumer: consumer);

        // Keep the task alive as long as the application runs
        return Task.Delay(Timeout.Infinite, stoppingToken);
    }

        public override void Dispose()
        {
            _channel?.Close();
            _connection?.Close();
            base.Dispose();
        }
    }
}