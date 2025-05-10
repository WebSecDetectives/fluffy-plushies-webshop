using MongoDB.Driver;
using OrderGraphQLApi.models;
using MongoDB.Bson;
using MongoDB.Driver.Linq;
using OrderGraphQLApi.graphql.input;

namespace OrderGraphQLApi.services;

public class OrderService
{
    private readonly RabbitMqService _RabbitMqService;
    private readonly IMongoCollection<Order> _orderCollection;

    public OrderService(IMongoDatabase database,  RabbitMqService rabbitMqService)
    {
        _orderCollection = database.GetCollection<Order>("orders");
        _RabbitMqService = rabbitMqService;
    }

 public async Task<Order?> GetOrderByIdAsync(string orderId)
{
    
    return await _orderCollection
        .Find(order => order.order_id == orderId) // Compare directly as strings
        .FirstOrDefaultAsync();
}

    public async Task<OrderConnection> GetOrdersAsync(int first, string? after)
    {   
        try
        {
            var query = _orderCollection.AsQueryable();

            if (!string.IsNullOrEmpty(after))
            {
                query = query.Where(o => string.Compare(o.order_id, after) > 0); // Compare as strings
            }

            var orders = await query
                .Take(first)
                .ToListAsync();

            var hasNextPage = orders.Count == first;
            var lastOrder = orders.LastOrDefault();
            var cursor = lastOrder != null ? lastOrder.order_id : null; // Direct string cursor

            return new OrderConnection
            {
                Edges = orders.Select(o => new OrderEdge { Node = o, Cursor = o.order_id }).ToList(),
                PageInfo = new PageInfo
                {
                    HasNextPage = hasNextPage,
                    EndCursor = cursor
                }
            };
        }
        catch (Exception ex)
        {
            // Log the exception here
            Console.WriteLine($"Error: {ex.Message}");
            return null;  // Or handle the error accordingly
        }
    }

    public async Task<Order> CreateOrderAsync(CreateOrderInput input)
    {
        var newOrder = new Order
        {
            order_id = Guid.NewGuid().ToString(),
            user_id = input.user_id,
            created_at = DateTime.UtcNow,
            customer_name = input.customer_name,
            address = input.address,
            line_items = input.line_items,
            shipping_cost = input.shipping_cost,
            total_amount = input.total_amount,
            status = "Pending"
        };

        await _orderCollection.InsertOneAsync(newOrder);

        _RabbitMqService.SendOrderCreatedEvent(newOrder);

        return newOrder;
    }

    public async Task<bool> DeleteOrderByIdAsync(string orderId)
    {
        var result = await _orderCollection.DeleteOneAsync(order => order.order_id == orderId);
        return result.DeletedCount > 0;  
    }
}