using OrderGraphQLApi.models;
using OrderGraphQLApi.services;
using OrderGraphQLApi.graphql.input;


namespace OrderGraphQLApi.graphql.mutations;

public class Mutation
{
    private readonly OrderService _orderService;

    public Mutation(OrderService orderService)
    {
        _orderService = orderService;
    }

    // resolvers
    public async Task<Order> CreateOrder(CreateOrderInput input, [Service] OrderService service)
    {
        return await service.CreateOrderAsync(input);
    }

    public async Task<Order> UpdateOrder(string orderId, UpdateOrderInput input, [Service] OrderService service)
    {
        return await service.UpdateOrderAsync(orderId, input);
    }

    public async Task<bool> DeleteOrder(string orderId, [Service] OrderService service)
    {
        return await service.DeleteOrderByIdAsync(orderId);
    }

    public async Task<bool> ConfirmOrder(ConfirmOrderInput input, [Service] OrderService service)
    {
        return await service.ConfirmOrderAsync(input);
    }
}
