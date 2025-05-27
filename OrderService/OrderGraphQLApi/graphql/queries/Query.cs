using OrderGraphQLApi.models;
using OrderGraphQLApi.services;



public class Query
{
    private readonly OrderService _orderService;

    public Query(OrderService orderService)
    {
        _orderService = orderService;
    }

    [GraphQLName("orderById")]
    public async Task<Order?> GetOrderById(string orderId)
    {
        return await _orderService.GetOrderByIdAsync(orderId);
    }

    [GraphQLName("orders")]
    public async Task<OrderConnection> GetOrders(int first = 10, string? after = null)
    {
        return await _orderService.GetOrdersAsync(first, after);
    }

    public string Ping() => "pong";
}