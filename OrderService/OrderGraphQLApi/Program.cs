using MongoDB.Driver;
using OrderGraphQLApi.graphql.mutations;
using OrderGraphQLApi.services;
using RabbitMQ.Client;
using DotNetEnv;

Env.Load();

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthorization();


// MONGODB INITIALIZATION

builder.Services.AddSingleton<IMongoClient>(sp =>
{
    var config = sp.GetRequiredService<IConfiguration>();
    var ConnectionString = Environment.GetEnvironmentVariable("MONGODB_CONNECTION_STRING");
    return new MongoClient(ConnectionString);
});


builder.Services.AddScoped(sp =>
{
    var config = sp.GetRequiredService<IConfiguration>();
    var client = sp.GetRequiredService<IMongoClient>();
    var databaseName = Environment.GetEnvironmentVariable("MONGODB_DATABASE_NAME");
    return client.GetDatabase(databaseName); // <-- this resolves IMongoDatabase
});

// MONGODB INITIALIZATION DONE


// RABBITMQ INITIALIZATION

builder.Services.AddSingleton<IConnection>(sp =>
{
    var factory = new ConnectionFactory
    {
        HostName = Environment.GetEnvironmentVariable("RABBITMQ_HOST"),
        UserName = Environment.GetEnvironmentVariable("RABBITMQ_USERNAME"),
        Password = Environment.GetEnvironmentVariable("RABBITMQ_PASSWORD"),
        Port = int.Parse(Environment.GetEnvironmentVariable("RABBITMQ_PORT") ?? "5672"),
        DispatchConsumersAsync = true
    };
    return factory.CreateConnection();
});

builder.Services.AddHostedService<UserInformationResponsesConsumer>();
builder.Services.AddHostedService<InventoryItemsReservationResponse>();
builder.Services.AddHostedService<RabbitMqConsumerService>(); // RABBITMQ LISTENERS

// RABBITMQ INITIALIZATION DONE



//builder.Services.AddHostedService<QueueConsumerService>();
builder.Services.AddSingleton<RabbitMqService>();

// GRAPHQL INITIALIZATION

builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>();

builder.Services.AddScoped<OrderService>();

// GRAPHQL INITIALIZATION DONE



// APP BUILDER CONFIG

var app = builder.Build();


app.UseHttpsRedirection();
app.UseAuthorization();

app.MapGraphQL();

app.Run();

// APP BUILDER CONFIG DONE