using OrderGraphQLApi.models;

namespace OrderGraphQLApi.graphql.input;

public class ConfirmOrderInput
{
    [GraphQLName("order_id")]
    public string? OrderId { get; set; }

    [GraphQLName("status")]
    public string? Status { get; set; }

}