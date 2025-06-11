using System.Text.Json;
using System.Text.Json.Nodes;

namespace OrderGraphQLApi.Utils
{
    public static class JsonReformatter
    {
        public static string ReformatCustomerJson(string jsonString)
        {
            var jsonObj = JsonNode.Parse(jsonString)?.AsObject();

            if (jsonObj == null)
                throw new ArgumentException("Invalid JSON string");

            var contactInfo = new JsonObject
            {
                ["customer_name"] = jsonObj["customer_name"]?.GetValue<string>(),
                ["email"] = jsonObj["email"]?.GetValue<string>(),
                ["phone"] = jsonObj["phone"]?.GetValue<long>()
            };

            var address = new JsonObject
            {
                ["street"] = jsonObj["street"]?.GetValue<string>(),
                ["postal_code"] = jsonObj["postal_code"]?.GetValue<int>(),
                ["city"] = jsonObj["city"]?.GetValue<string>(),
                ["country"] = jsonObj["country"]?.GetValue<string>()
            };

            var reformatted = new JsonObject
            {
                ["contact_information"] = contactInfo,
                ["address"] = address
            };

            var options = new JsonSerializerOptions
            {
                WriteIndented = true
            };

            return reformatted.ToJsonString(options);
        }
    }
}