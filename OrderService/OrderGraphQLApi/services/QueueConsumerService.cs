using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using OrderGraphQLApi.services;


namespace OrderGraphQLApi.services;

public class QueueConsumerService : BackgroundService
{
    private readonly IModel _channel;
    private readonly RabbitMqService _rabbitMqService;

    public QueueConsumerService(IModel channel, RabbitMqService rabbitMqService)
    {
        _channel = channel;
        _rabbitMqService = rabbitMqService;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var consumer = new EventingBasicConsumer(_channel);

        consumer.Received += async (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var message = Encoding.UTF8.GetString(body);

            // Handle message asynchronously
            await _rabbitMqService.ManageResponse(message);
        };

        _channel.BasicConsume("", autoAck: true, consumer: consumer);

        return Task.CompletedTask;
    }
}
