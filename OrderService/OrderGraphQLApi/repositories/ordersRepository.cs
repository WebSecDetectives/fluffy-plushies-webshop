/*using MongoDB.Driver;
using OrderGraphQLApi.models;

namespace repositories.ordersRepository;

public class OrdersRepository
{
    private readonly IMongoCollection<Order> _orders;

    public OrdersRepository(IMongoClient mongoClient, IConfiguration config)
    {
        var databaseName = config.GetSection("MongoDbSettings:DatabaseName").Value;
        var database = mongoClient.GetDatabase(databaseName);
        _orders = database.GetCollection<Order>("orders");
    }

    public async Task<List<Order>> GetAllAsync()
    {
        var orders = await _orders.Find(_ => true).ToListAsync();
        return orders;
    }
    
    public async Task CreateAsync(Order order)
    {
        await _orders.InsertOneAsync(order);
    }
    

}
*/