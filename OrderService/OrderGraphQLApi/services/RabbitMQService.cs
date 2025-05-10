using RabbitMQ.Client;
using System.Text;
using System.Text.Json;
using OrderGraphQLApi.models;

namespace OrderGraphQLApi.services;

public class RabbitMqService
{
    private readonly IConnection _connection;
    private readonly IModel _channel;
    private readonly string _exchangeName = "order_exchange";

    public RabbitMqService()
    {
        var factory = new ConnectionFactory() { HostName = "localhost" };
        _connection = factory.CreateConnection();
        _channel = _connection.CreateModel();
        _channel.ExchangeDeclare(_exchangeName, "direct", durable: true);
    }

    // Send Order Event to the Queue
    public void SendOrderCreatedEvent(Order order)
    {
        var message = JsonSerializer.Serialize(order);
        var body = Encoding.UTF8.GetBytes(message);

        _channel.BasicPublish(exchange: _exchangeName,
                              routingKey: "order_created",
                              basicProperties: null,
                              body: body);
    }
    
    public void Close()
    {
        _channel.Close();
        _connection.Close();
    }
}