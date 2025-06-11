using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using System.Text.Json.Serialization;

namespace OrderGraphQLApi.models;

public class Order
{
    [BsonId]
    public ObjectId? _id { get; set; }
    public string? order_id { get; set; }
    public string? user_id { get; set; }
    public DateTime? created_at { get; set; }
    public string? customer_name { get; set; }
    public address? address { get; set; }
    public contact_information? contact_information { get; set; }
    public List<line_item>? line_items { get; set; }
    public decimal? shipping_cost { get; set; }
    public decimal? total_amount { get; set; }
    public string? status { get; set; }
}


public class line_item
{
    [JsonPropertyName("item_id")]
    public string? item_id { get; set; }

    [JsonPropertyName("item_name")]
    public string? item_name { get; set; }

    [JsonPropertyName("quantity")]
    public int? quantity { get; set; }

    [JsonPropertyName("price_per_item")]
    public decimal? price_per_item { get; set; }
}

public class LineItemsWrapper
{
    [JsonPropertyName("line_items")] // change to line_items if necessary
    public List<line_item>? LineItems { get; set; }
}

public class address
{
    public string? street { get; set; }
    public int? postal_code { get; set; }
    public string? city { get; set; }
    public string? country { get; set; }
}

public class contact_information
{
    public string? customer_name { get; set; }
    public string? email { get; set; }
    public long? phone { get; set; }
}