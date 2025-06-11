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

            // Check if collection is empty
            var hasData = await ordersCollection.Find(_ => true).AnyAsync();
            if (hasData)
            {
                logger.LogInformation("Orders collection already seeded. Skipping.");
                return;
            }

            logger.LogInformation("Seeding MongoDB orders collection...");

            var seedOrder = new Order
            {
                _id = ObjectId.GenerateNewId(),
                order_id = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                contact_information = new contact_information
                {
                    email = "admin@admin.com"
                },
                line_items = new List<line_item>
                {
                    new line_item
                    {
                        item_id = "550e8400-e29b-41d4-a716-446655440000",
                        quantity = 2,
                    },
                    new line_item
                    {
                        item_id = "550e8400-e29b-41d4-a716-446655440001",
                        quantity = 5,
                    }
                },
                status = "editable"
                // All other fields are null or omitted
            };

            await ordersCollection.InsertOneAsync(seedOrder);

            logger.LogInformation("MongoDB seeding complete.");
        }
    }
}