using MongoDB.Driver;
using OrderGraphQLApi.graphql.mutations;
using OrderGraphQLApi.services;
using RabbitMQ.Client;
using DotNetEnv;

Env.Load();

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthorization();

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

builder.Services.AddSingleton<IConnectionFactory>(_ => new ConnectionFactory
{
    HostName = Environment.GetEnvironmentVariable("RABBITMQ_HOST_NAME"),        // or your RabbitMQ container name if using Docker Compose
    UserName = Environment.GetEnvironmentVariable("RABBITMQ_USERNAME"),
    Password = Environment.GetEnvironmentVariable("RABBITMQ_PASSWORD"),
    DispatchConsumersAsync = true // enables async consumers (optional, good practice)
});


builder.Services.AddHostedService<QueueConsumerService>();

builder.Services.AddSingleton<RabbitMqService>();

builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>();

builder.Services.AddScoped<OrderService>();

var app = builder.Build();



app.UseHttpsRedirection();
app.UseAuthorization();



app.MapGraphQL();                             

app.Run();