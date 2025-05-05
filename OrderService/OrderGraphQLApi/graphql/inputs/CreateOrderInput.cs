namespace OrderGraphQLApi.GraphQL.Inputs;

public record CreateOrderInput(string ProductId, int Quantity, string CustomerId);
