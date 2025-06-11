using System;
using System.Collections.Generic;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using OrderGraphQLApi.models;

namespace OrderGraphQLApi.services
{

    public class InventoryMockResponder : BackgroundService
    {
        private readonly IConnection _connection;
        private readonly IModel _channel;

        private const string ListenQueue = "inventory.items_reservation_requests";
        private const string RespondExchange = "order_exchange";
        private const string RespondRoutingKey = "inventory.items_reservation_responses";

        private readonly Dictionary<string, (string Name, decimal Price, int Stock)> _products = new()
    {
        { "550e8400-e29b-41d4-a716-446655440000", ("Fluffy", 19.99m, 73) },
        { "550e8400-e29b-41d4-a716-446655440001", ("Flushy the Bear", 24.99m, 92) },
        { "550e8400-e29b-41d4-a716-446655440002", ("Pawbert the Puppy", 21.49m, 58) },
        { "550e8400-e29b-41d4-a716-446655440003", ("Snuggs the Sloth", 29.95m, 50) },
        { "550e8400-e29b-41d4-a716-446655440004", ("Mallow the Bunny", 17.99m, 76) },
        { "550e8400-e29b-41d4-a716-446655440005", ("Waddles the Penguin", 22.49m, 64) },
        { "550e8400-e29b-41d4-a716-446655440006", ("Roary the Dino", 27.99m, 52) }
    };


        public InventoryMockResponder(IConnection connection)
        {
            _connection = connection;
            _channel = _connection.CreateModel();

            _channel.QueueDeclare(ListenQueue, durable: true, exclusive: false, autoDelete: false, arguments: null);
            _channel.ExchangeDeclare(RespondExchange, ExchangeType.Direct, durable: true);
            _channel.QueueBind(ListenQueue, RespondExchange, ListenQueue);
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            var consumer = new AsyncEventingBasicConsumer(_channel);

            consumer.Received += async (model, ea) =>
            {
                var body = ea.Body.ToArray();
                var incomingMessage = Encoding.UTF8.GetString(body);

                var correlationId = ea.BasicProperties?.CorrelationId;
                if (string.IsNullOrEmpty(correlationId) && ea.BasicProperties?.Headers != null &&
                    ea.BasicProperties.Headers.TryGetValue("correlation_id", out var headerVal))
                {
                    correlationId = Encoding.UTF8.GetString((byte[])headerVal);
                }

                Console.WriteLine($"[Mock] Received inventory request: {incomingMessage} (CorrelationId: {correlationId})");

                var request = JsonSerializer.Deserialize<ItemsReservationRequestDto>(incomingMessage);
                Console.WriteLine($"Request DTO Type: {request.GetType().FullName}");

                var lineItemsResponse = new List<object>();

                bool hasEnoughStock = true;

                foreach (var item in request.items)
                {
                    if (_products.TryGetValue(item.item_id, out var product))
                    {
                        if (product.Stock < item.quantity)
                        {
                            hasEnoughStock = false;
                            break;
                        }
                        lineItemsResponse.Add(new
                        {
                            item_id = item.item_id,
                            item_name = product.Name,
                            quantity = item.quantity,
                            price_per_item = product.Price
                        });
                    }
                    else
                    {
                        hasEnoughStock = false;
                        break;
                    }
                }

                object responsePayload;

                if (hasEnoughStock)
                {
                    responsePayload = new
                    {
                        line_items = lineItemsResponse
                    };
                }
                else
                {
                    responsePayload = new
                    {
                        error = "Insufficient stock for one or more items."
                    };
                }

                var responseBytes = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(responsePayload));

                var props = _channel.CreateBasicProperties();
                props.Headers = new Dictionary<string, object>
                {
                { "correlation_id", Encoding.UTF8.GetBytes(correlationId ?? "") }
                };
                props.MessageId = hasEnoughStock ? "inventory_response" : "inventory_response_error";

                _channel.BasicPublish(
                    exchange: RespondExchange,
                    routingKey: RespondRoutingKey,
                    basicProperties: props,
                    body: responseBytes
                );

                Console.WriteLine("[Mock] Sent inventory response");

                _channel.BasicAck(ea.DeliveryTag, false);

                await Task.CompletedTask;
            };

            _channel.BasicConsume(queue: ListenQueue, autoAck: false, consumer: consumer);

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