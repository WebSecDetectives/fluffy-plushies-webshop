namespace OrderGraphQLApi.graphql.input;

public class UpdateOrderInput
{
    [GraphQLName("order_id")]
    public string? OrderId { get; set; }

    [GraphQLName("customer_name")]
    public string? CustomerName { get; set; }

    [GraphQLName("address")]
    public UpdateAddressInput? Address { get; set; }

    [GraphQLName("line_items")]
    public List<UpdateLineItemInput>? LineItems { get; set; }

    [GraphQLName("shipping_cost")]
    public double? ShippingCost { get; set; }

    [GraphQLName("total_amount")]
    public double? TotalAmount { get; set; }

    [GraphQLName("status")]
    public string? Status { get; set; }
}

public class UpdateLineItemInput
{
    [GraphQLName("item_id")]
    public string? ItemId { get; set; }

    [GraphQLName("item_name")]
    public string? ItemName { get; set; }

    [GraphQLName("quantity")]
    public int? Quantity { get; set; }

    [GraphQLName("price_per_item")]
    public double? PricePerItem { get; set; }
}

public class UpdateAddressInput
{
    [GraphQLName("street")]
    public string? Street { get; set; }

    [GraphQLName("postal_code")]
    public int? PostalCode { get; set; }

    [GraphQLName("city")]
    public string? City { get; set; }

    [GraphQLName("country")]
    public string? Country { get; set; }
}