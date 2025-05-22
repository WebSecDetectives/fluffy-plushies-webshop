using OrderGraphQLApi.models;

namespace OrderGraphQLApi.graphql.input;

public class CreateOrderInput
{
    public string? user_id { get; set; }
    public string? customer_name { get; set; }
    public address? address { get; set; }
    public List<line_item>? line_items { get; set; }
    public double? shipping_cost { get; set; }
    public double? total_amount { get; set; }
    public string? status { get; set; }
}