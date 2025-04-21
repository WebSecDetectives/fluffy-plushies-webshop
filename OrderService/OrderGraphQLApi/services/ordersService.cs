using models.order;
using repositories.ordersRepository;
using RabbitMQ.Client;
using Microsoft.AspNetCore.Mvc;

namespace services.ordersService;

public class OrdersService
{
    private readonly OrdersRepository _ordersRepository;

    public OrdersService(OrdersRepository ordersRepository)
    {
        _ordersRepository = ordersRepository;
    }

    public async Task<Order> CreateAsync(Order order)
    {
        if(order.line_items == null || order.line_items.Count == 0){
            throw new InvalidOperationException("Order must have at least one line item");
        }
        
        await _ordersRepository.CreateAsync(order);

        return order;
        
    }

    public async Task<List<Order>> GetAllAsync()
    {
        return await _ordersRepository.GetAllAsync();
     }
}