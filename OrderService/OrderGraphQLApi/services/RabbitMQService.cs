using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Text.Json;
using OrderGraphQLApi.models;

namespace OrderGraphQLApi.services;

public class RabbitMqService
{
    private readonly IConnection _connection;
    private readonly IModel _channel;
    private readonly string _exchangeName = "order_exchange";

    public RabbitMqService(IConnection connection)
    {


        _connection = connection;
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

        _channel.QueueDeclare(queue: "inventory.items_reservation_requests",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);

        _channel.QueueBind(queue: "inventory.items_reservation_requests",
                           exchange: _exchangeName,
                           routingKey: "inventory.items_reservation_requests");
/* 
        _channel.QueueDeclare(queue: "identity.user_information_requests",
                              durable: true,
                              exclusive: false,
                              autoDelete: false,
                              arguments: null);
*/
        _channel.QueueBind(queue: "identity.user_information_requests",
                            exchange: _exchangeName,
                            routingKey: "identity.user_information_requests");
                           
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

    public void GetUserInfo(string jwt, string correlationId)
{
    var messageObject = new { user_token = jwt };
    var messageJson = JsonSerializer.Serialize(messageObject);
    var body = Encoding.UTF8.GetBytes(messageJson);

    var properties = _channel.CreateBasicProperties();
    //properties.CorrelationId = correlationId;
    properties.MessageId = "user_information_request";
    properties.Headers = new Dictionary<string, object> { { "correlation_id", correlationId } };

    Console.WriteLine("calling identity service");

    _channel.BasicPublish(exchange: _exchangeName,
                         routingKey: "identity.user_information_requests",
                         basicProperties: properties,
                         body: body);
}

    public void CheckInventory(ItemsReservationRequestDto lineItems, string correlationId)
{
    var message = JsonSerializer.Serialize(lineItems);
    var body = Encoding.UTF8.GetBytes(message);
    var properties = _channel.CreateBasicProperties();
    //properties.CorrelationId = correlationId;
    properties.Headers = new Dictionary<string, object> { { "correlation_id", correlationId } };

    var d = Encoding.UTF8.GetString(body);
    Console.WriteLine(d);

    _channel.BasicPublish(exchange: _exchangeName, // to inventory service
                         routingKey: "inventory.items_reservation_requests",
                         basicProperties: properties,
                         body: body);
}

    public void SendOrderConfirmedEvent(string orderFull)
    {
       // var message = JsonSerializer.Serialize(orderFull);
        var body = Encoding.UTF8.GetBytes(orderFull);

        var m = Encoding.UTF8.GetString(body);
        Console.WriteLine(m);

        _channel.BasicPublish(exchange: _exchangeName,
                              routingKey: "order.confirmed",
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

    


    public async void SendOrderDeletedEvent(string orderId)
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
    
}