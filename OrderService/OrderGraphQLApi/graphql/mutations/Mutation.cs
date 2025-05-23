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

    public async Task<Order> CreateOrder(CreateOrderInput input) =>
        await _orderService.CreateOrderAsync(input);

    public async Task<Order> UpdateOrder(string orderId, UpdateOrderInput input) =>
        await _orderService.UpdateOrderAsync(orderId, input);

    public async Task<bool> DeleteOrder(string orderId) =>
        await _orderService.DeleteOrderByIdAsync(orderId);

    public async Task<string> ConfirmOrder(ConfirmOrderInput input) =>
        await _orderService.StartOrderConfirmation(input);
}