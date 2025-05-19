using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace OrderGraphQLApi.models;

public class Order{
    [BsonId]
    public ObjectId? _id { get; set; }
    public string? order_id { get; set; }
    public string? user_id { get; set; }
    public DateTime? created_at { get; set; }
    public string? customer_name { get; set; }
    public Address? address { get; set; }
    public List<line_item>? line_items { get; set; }
    public double? shipping_cost { get; set; }
    public double? total_amount { get; set; }
    public string? status { get; set; }
}

public class line_item{
    public string? item_id { get; set; }
    public string? item_name { get; set; }
    public int? quantity { get; set; }
    public double? price_per_item { get; set; }
}

public class Address{
    public string? street { get; set; }
    public int? postal_code { get; set; }
    public string? city { get; set; }
    public string? country { get; set; }
}