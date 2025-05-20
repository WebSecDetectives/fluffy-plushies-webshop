using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Text.Json;
using OrderGraphQLApi.models;

namespace OrderGraphQLApi.services;

public class RabbitMqService
{
    private EventingBasicConsumer _consumer;

    private readonly IConnection _connection;
    private readonly IModel _channel;
    private readonly string _exchangeName = "order_exchange";

    public RabbitMqService()
    {
        var factory = new ConnectionFactory() { HostName = Environment.GetEnvironmentVariable("RABBITMQ_HOST_NAME"), DispatchConsumersAsync = true };
        _connection = factory.CreateConnection();
        _channel = _connection.CreateModel();


        _channel.ExchangeDeclare(_exchangeName, "direct", durable: true);


        _channel.QueueDeclare(queue: "order_created_queue",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);

        _channel.QueueBind(queue: "order_created_queue",
                           exchange: _exchangeName,
                           routingKey: "order_created");

        _channel.QueueDeclare(queue: "order_updated_queue",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);

        _channel.QueueBind(queue: "order_updated_queue",
                           exchange: _exchangeName,
                           routingKey: "order_updated");

        _channel.QueueDeclare(queue: "order_deleted_queue",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);

        _channel.QueueBind(queue: "order_deleted_queue",
                           exchange: _exchangeName,
                           routingKey: "order_deleted");

        _channel.QueueDeclare(queue: "order.confirmed",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);

        _channel.QueueBind(queue: "order.confirmed",
                           exchange: _exchangeName,
                           routingKey: "order.confirmed");

        _channel.QueueDeclare(queue: "inventory.items.reserve",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);

        _channel.QueueBind(queue: "inventory.items.reserve",
                           exchange: _exchangeName,
                           routingKey: "inventory.items.reserve");

    }

    

    public void SendOrderCreatedEvent(Order order)
    {
        var message = JsonSerializer.Serialize(order);
        var body = Encoding.UTF8.GetBytes(message);

        _channel.BasicPublish(exchange: _exchangeName,
                              routingKey: "order_created",
                              basicProperties: null,
                              body: body);
    }

    public void SendOrderUpdatedEvent(Order order)
    {
        var message = JsonSerializer.Serialize(order);
        var body = Encoding.UTF8.GetBytes(message);

        _channel.BasicPublish(exchange: _exchangeName,
                              routingKey: "order_updated",
                              basicProperties: null,
                              body: body);
    }

    public void SendOrderConfirmedEvent(ItemsReservationRequestDto order)
    {
        var message = JsonSerializer.Serialize(order);
        var body = Encoding.UTF8.GetBytes(message);

        _channel.BasicPublish(exchange: _exchangeName, // to inventory service
                              routingKey: "inventory.items.reserve",
                              basicProperties: null,
                              body: body);
    }

    public async Task ManageResponse(string message)
    {
        var body = Encoding.UTF8.GetBytes(message);
        
        _channel.BasicPublish(exchange: _exchangeName, // to email service
                              routingKey: "order.confirmed",
                              basicProperties: null,
                              body: body);
    }

    


    public void SendOrderDeletedEvent(string orderId)
    {
        var message = JsonSerializer.Serialize(orderId);
        var body = Encoding.UTF8.GetBytes(message);

        _channel.BasicPublish(exchange: _exchangeName,
                              routingKey: "order_deleted",
                              basicProperties: null,
                              body: body);
    }

    public void Close()
    {
        _channel.Close();
        _connection.Close();
    }
    
     public Task PublishAsync<T>(string routingKey, T message, IDictionary<string, object>? headers = null)
    {
        var props = _channel.CreateBasicProperties();
        props.ContentType = "application/json";

        if (headers != null)
        {
            props.Headers = headers.ToDictionary(kvp => kvp.Key, kvp => (object)kvp.Value);
        }

        var json = JsonSerializer.Serialize(message);
        var body = Encoding.UTF8.GetBytes(json);

        _channel.BasicPublish(
            exchange: _exchangeName,
            routingKey: routingKey,
            basicProperties: props,
            body: body
        );

        return Task.CompletedTask;
    }
}