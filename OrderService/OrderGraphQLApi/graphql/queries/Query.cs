using OrderGraphQLApi.models;
using OrderGraphQLApi.services;



public class Query
{
    private readonly OrderService _orderService;

    public Query(OrderService orderService)
    {
        _orderService = orderService;
    }

    // Resolvers
    [GraphQLName("orderById")]
    public async Task<Order?> GetOrderById(string orderId, [Service] OrderService orderService)
    {
        return await orderService.GetOrderByIdAsync(orderId);
    }

   public async Task<OrderConnection> GetOrders([Service] OrderService orderService,int first = 10, string? after = null)
    {
        return await orderService.GetOrdersAsync(first, after);
    }
}