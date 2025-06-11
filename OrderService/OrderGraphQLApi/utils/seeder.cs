using OrderGraphQLApi.models;
using MongoDB.Driver;
using MongoDB.Bson;
using Microsoft.Extensions.Logging;

namespace OrderGraphQLApi.Utils
{
    public static class MongoSeeder
    {
       public static async Task SeedDataAsync(IMongoDatabase database, ILogger logger)
{
    var ordersCollection = database.GetCollection<Order>("orders");

    // Clear existing orders
    logger.LogInformation("Clearing existing orders...");
    await ordersCollection.DeleteManyAsync(Builders<Order>.Filter.Empty);

    logger.LogInformation("Seeding MongoDB orders collection...");

    // Define product IDs
    var productIds = new List<string>
    {
        "550e8400-e29b-41d4-a716-446655440000", // Fluffy
        "550e8400-e29b-41d4-a716-446655440001", // Flushy the Bear
        "550e8400-e29b-41d4-a716-446655440002", // Pawbert the Puppy
        "550e8400-e29b-41d4-a716-446655440003", // Snuggs the Sloth
        "550e8400-e29b-41d4-a716-446655440004", // Mallow the Bunny
        "550e8400-e29b-41d4-a716-446655440005", // Waddles the Penguin
        "550e8400-e29b-41d4-a716-446655440006"  // Roary the Dino
    };

    var random = new Random();

    var orders = new List<Order>();

    for (int i = 70; i <= 79; i++)
    {
        var order = new Order
        {
            _id = ObjectId.GenerateNewId(),
            order_id = $"f47ac10b-58cc-4372-a567-0e02b2c3d4{i}", // Ends in 70-79
            contact_information = new contact_information
            {
                email = "admin@admin.com"
            },
            line_items = new List<line_item>(),
            status = "editable"
        };

        // Add 1 to 3 random line items
        var itemCount = random.Next(1, 4);
        var selectedItems = productIds.OrderBy(_ => random.Next()).Take(itemCount);

        foreach (var itemId in selectedItems)
        {
            order.line_items.Add(new line_item
            {
                item_id = itemId,
                quantity = random.Next(1, 6) // Quantity 1 to 5
            });
        }

        orders.Add(order);
    }

    await ordersCollection.InsertManyAsync(orders);

    logger.LogInformation("MongoDB seeding complete.");
}
    }
}