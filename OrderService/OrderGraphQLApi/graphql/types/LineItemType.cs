using HotChocolate.Types;
using OrderGraphQLApi.models;

namespace OrderGraphQLApi.GraphQL.Types;

public class LineItemType : ObjectType<line_item>
{
    protected override void Configure(IObjectTypeDescriptor<line_item> descriptor)
    {
        descriptor.Description("Represents a single item in an order.");

        descriptor.Field(li => li.item_id);
        descriptor.Field(li => li.item_name);
        descriptor.Field(li => li.quantity);
        descriptor.Field(li => li.price_per_item);
    }
}