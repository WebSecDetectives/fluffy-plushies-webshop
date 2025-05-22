using OrderGraphQLApi.models;

namespace OrderGraphQLApi.graphql.input;

public class ConfirmOrderInput
{
    [GraphQLName("order_id")]
    public string? order_id { get; set; }

    [GraphQLName("user_token")]
    public string? user_token { get; set; }
}