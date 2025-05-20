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

    public OrderService(IMongoDatabase database, RabbitMqService rabbitMqService)
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
        try
        {
            var newOrder = new Order
            {
                _id = ObjectId.GenerateNewId(),
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
        catch (Exception ex)
        {

            Console.WriteLine($"Error creating order: {ex}");
            return null;
        }
    }

    public async Task<bool> DeleteOrderByIdAsync(string orderId)
    {
        var result = await _orderCollection.DeleteOneAsync(order => order.order_id == orderId);

        if (result.DeletedCount > 0)
        {
            _RabbitMqService.SendOrderDeletedEvent(orderId);
        }

        return result.DeletedCount > 0;
    }

    public async Task<Order?> UpdateOrderAsync(string orderId, UpdateOrderInput input)
    {
        // 1. Find existing order
        var order = await _orderCollection
            .Find(o => o.order_id == orderId)
            .FirstOrDefaultAsync();

        if (order == null)
            return null; // or throw if you prefer

        // 2. Update fields if present in input

        if (!string.IsNullOrEmpty(input.CustomerName))
            order.customer_name = input.CustomerName;

        if (input.Address != null)
        {
            if (order.address == null)
                order.address = new Address();

            order.address.street = input.Address.Street ?? order.address.street;
            order.address.postal_code = input.Address.PostalCode ?? order.address.postal_code;
            order.address.city = input.Address.City ?? order.address.city;
            order.address.country = input.Address.Country ?? order.address.country;
        }

        if (input.LineItems != null)
        {
            order.line_items = input.LineItems.Select(li => new line_item
            {
                item_id = li.ItemId,
                item_name = li.ItemName,
                quantity = li.Quantity,
                price_per_item = li.PricePerItem
            }).ToList();
        }

        if (input.ShippingCost.HasValue)
            order.shipping_cost = input.ShippingCost.Value;

        if (input.TotalAmount.HasValue)
            order.total_amount = input.TotalAmount.Value;

        if (!string.IsNullOrEmpty(input.Status))
            order.status = input.Status;

        // 3. Replace the order document in MongoDB
        var replaceResult = await _orderCollection
            .ReplaceOneAsync(o => o.order_id == orderId, order);

        // 4. Optionally check if update succeeded
        if (!replaceResult.IsAcknowledged || replaceResult.ModifiedCount == 0)
            return null;

        _RabbitMqService.SendOrderUpdatedEvent(order);

        return order;
    }

    public async Task<bool> ConfirmOrderAsync(ConfirmOrderInput input)
    {
        if (input.Status?.ToLower() != "confirmed")
            return false;

        var order = await _orderCollection
            .Find(o => o.order_id == input.OrderId)
            .FirstOrDefaultAsync();

        if (order == null)
            return false;

        var request = new ItemsReservationRequestDto
         {
            items = order.line_items.Select(item => new ReservationItem {
            item_id = item.item_id,
            quantity = (int)item.quantity
        }).ToList()
            };

        _RabbitMqService.SendOrderConfirmedEvent(request);

        return true;        
    }
}