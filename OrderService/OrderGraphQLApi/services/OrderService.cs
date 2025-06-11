using MongoDB.Driver;
using OrderGraphQLApi.models;
using MongoDB.Bson;
using MongoDB.Driver.Linq;
using OrderGraphQLApi.graphql.input;
using System.Text.Json;
using System.Text;
using OrderGraphQLApi.Utils;

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
                shipping_cost = (decimal)(input.shipping_cost ?? 0),
                total_amount = (decimal)(input.total_amount ?? 0),
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
                order.address = new address();

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
                quantity = (int)li.Quantity,
                price_per_item = (decimal)li.PricePerItem
            }).ToList();
        }

        if (input.ShippingCost.HasValue)
            order.shipping_cost = (decimal)input.ShippingCost.Value;

        if (input.TotalAmount.HasValue)
            order.total_amount = (decimal)input.TotalAmount.Value;

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

    public async Task<string> StartOrderConfirmation(ConfirmOrderInput input)
{
    if (input.order_id == null || input.user_token == null)
    {
        return "Missing order_id or user_token";
    }

    var filter = Builders<Order>.Filter.Eq(o => o.order_id, input.order_id);
    var order = await _orderCollection.Find(filter).FirstOrDefaultAsync();

    if (order == null)
    {
        return "Order not found";
    }

    if (order.status == "awaiting authentication")
    {
        // Already in the desired state, so treat as idempotent success
        return "Order confirmation already started";
    }
    
    if (order.status != "editable")
    {
        // Optionally handle invalid transitions, e.g., reject or log warning
        return $"Cannot start confirmation from status '{order.status}'";
    }

    var update = Builders<Order>.Update.Set(o => o.status, "awaiting authentication");
    await _orderCollection.UpdateOneAsync(filter, update);

    _RabbitMqService.GetUserInfo(input.user_token, input.order_id);

    return "Order confirmation started";
}

    public async Task getLineItemsByOrderId(string message, string order_id)
    {
        Console.WriteLine("Method started");
        Console.WriteLine(order_id);

        message = JsonReformatter.ReformatCustomerJson(message);

        Console.WriteLine(message);

        var filter = Builders<Order>.Filter.Eq("order_id", order_id);

        var order = await _orderCollection.Find(filter).FirstOrDefaultAsync();


        if (order == null)
        {
            throw new ArgumentException($"Order with that ID not found.");
        }

        Console.WriteLine("Order found");

        var doc = JsonDocument.Parse(message).RootElement;

        try
        {
            var address = JsonSerializer.Deserialize<address>(doc.GetProperty("address").GetRawText());
            var contactInfo = JsonSerializer.Deserialize<contact_information>(doc.GetProperty("contact_information").GetRawText());

            Console.WriteLine("Parsed address: " + JsonSerializer.Serialize(address));
            Console.WriteLine("Parsed contact info: " + JsonSerializer.Serialize(contactInfo));

            var update = Builders<Order>.Update
                .Set("address", address)
                .Set("contact_information", contactInfo);

            var result = await _orderCollection.UpdateOneAsync(filter, update);
            Console.WriteLine($"Update result: Matched={result.MatchedCount}, Modified={result.ModifiedCount}");
        }
        catch (Exception ex)
        {
            Console.WriteLine("Deserialization or update error: " + ex.Message);
            return;
        }

        Console.WriteLine("Creating DTO...");
        var dto = new ItemsReservationRequestDto
        {
            items = order.line_items.Select(item => new ReservationItem
            {
                item_id = item.item_id,
                quantity = (int)item.quantity
            }).ToList()
        };

        int x = 1;
        foreach (var item in dto.items)
        {
            Console.WriteLine($"Item {x}: {JsonSerializer.Serialize(item)}");
            x++;
        }
        

        _RabbitMqService.CheckInventory(dto, order_id);
        Console.WriteLine("All done");

    }


    public async Task FinalizeOrder(string items, string order_id)
    {

        var options = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        };

        var wrapper = JsonSerializer.Deserialize<LineItemsWrapper>(items);
        List<line_item> line_items = wrapper.LineItems;


        decimal total_amount = line_items.Sum(i => (decimal)i.quantity * (decimal)i.price_per_item);

        decimal shipping_cost = total_amount * (decimal)0.03;


        var order = await _orderCollection.Find(o => o.order_id == order_id).FirstOrDefaultAsync();


        var filter = Builders<Order>.Filter.Eq(o => o.order_id, order_id);
        var update = Builders<Order>.Update.Set(o => o.status, "confirmed")
            .Set(o => o.line_items, line_items)
            .Set(o => o.total_amount, total_amount)
            .Set(o => o.shipping_cost, shipping_cost);
        var result = await _orderCollection.UpdateOneAsync(filter, update);


        var orders = await _orderCollection.Find(filter).FirstOrDefaultAsync();

        var response = new
        {
            orders.line_items,
            orders.address,
            orders.contact_information,
            orders.total_amount,
            orders.shipping_cost,
            orders.status
        };
        string finalJson = JsonSerializer.Serialize(response, new JsonSerializerOptions { WriteIndented = true });

        Console.WriteLine("final json outbound for email service");

        Console.WriteLine(finalJson);

        _RabbitMqService.SendOrderConfirmedEvent(finalJson);
    }


}