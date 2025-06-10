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
    public class InventoryItemsReservationResponse : BackgroundService
    {
        private readonly IConnection _connection;
        private readonly IModel _channel;
        private readonly ILogger<InventoryItemsReservationResponse> _logger;
        private readonly IServiceScopeFactory _serviceScopeFactory;

        private const string ExchangeName = "order_exchange";
        private const string QueueName = "inventory.items_reservation_responses";

        public InventoryItemsReservationResponse(
            IConnection connection,
            ILogger<InventoryItemsReservationResponse> logger,
            IServiceScopeFactory serviceScopeFactory)
        {
            _connection = connection;
            _channel = _connection.CreateModel();
            _logger = logger;
            _serviceScopeFactory = serviceScopeFactory;

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
        var correlationId = ea.BasicProperties?.CorrelationId;

        // Check for correlation_id in headers if CorrelationId is not set
        if (string.IsNullOrEmpty(correlationId) && ea.BasicProperties?.Headers != null &&
            ea.BasicProperties.Headers.TryGetValue("correlation_id", out var headerValue))
        {
            correlationId = Encoding.UTF8.GetString((byte[])headerValue);
        }

        var messageId = ea.BasicProperties?.MessageId;

        if (messageId == "items_reservation_response_error")
        {
            _logger.LogError($"[InventoryItemsReservationResponse] Received message from {QueueName}: {message}");
            return;
        }

        if (string.IsNullOrEmpty(correlationId))
        {
            _logger.LogWarning("CorrelationId is missing, message will be acknowledged and skipped.");
            _channel.BasicAck(ea.DeliveryTag, false);
            return;
        }

        try
        {
            
            Console.WriteLine("Message received: " + message);

            using var scope = _serviceScopeFactory.CreateScope();
            var orderService = scope.ServiceProvider.GetRequiredService<OrderService>();

            await orderService.FinalizeOrder(message, (string)correlationId);

            _channel.BasicAck(ea.DeliveryTag, false);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error processing message with CorrelationId {CorrelationId}", correlationId);
        }
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
