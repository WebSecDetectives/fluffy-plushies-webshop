using HotChocolate.Types;
using OrderGraphQLApi.models;

public class AddressType : ObjectType<Address>
{
    protected override void Configure(IObjectTypeDescriptor<Address> descriptor)
    {
        descriptor.Description("Represents a delivery address.");

        descriptor.Field(a => a.street);
        descriptor.Field(a => a.postal_code);
        descriptor.Field(a => a.city);
        descriptor.Field(a => a.country);
    }
}