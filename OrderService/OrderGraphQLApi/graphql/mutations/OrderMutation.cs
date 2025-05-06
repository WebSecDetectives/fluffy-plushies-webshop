using models.order;

namespace OrderGraphQLApi.graphql.mutations;

public class Mutation
{
    public Order CreateOrder(string productName, int quantity)
    {
        return new Order
        {
            order_id = Guid.NewGuid().ToString(),
            
        };
    }
}
