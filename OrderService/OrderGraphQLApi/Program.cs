using MongoDB.Driver;
using OrderGraphQLApi.graphql.mutations;
using OrderGraphQLApi.services;
using RabbitMQ.Client;
using DotNetEnv;
using Microsoft.Extensions.Logging;
using OrderGraphQLApi.Utils;

Env.Load();

var builder = WebApplication.CreateBuilder(args);

// Clear default providers and add Console logging for better visibility inside Docker
builder.Logging.ClearProviders();
builder.Logging.AddConsole();

builder.Services.AddAuthorization();

// MONGODB INITIALIZATION

builder.Services.AddSingleton<IMongoClient>(sp =>
{
    var logger = sp.GetRequiredService<ILogger<Program>>();
    var connectionString = Environment.GetEnvironmentVariable("MONGODB_CONNECTION_STRING");
    logger.LogInformation("MongoDB connection string: {ConnectionString}", connectionString);

    return new MongoClient(connectionString);
});

builder.Services.AddScoped(sp =>
{
    var logger = sp.GetRequiredService<ILogger<Program>>();
    var client = sp.GetRequiredService<IMongoClient>();
    var databaseName = Environment.GetEnvironmentVariable("MONGODB_DATABASE_NAME");
    logger.LogInformation("MongoDB database name: {DatabaseName}", databaseName);

    return client.GetDatabase(databaseName);
});

// RABBITMQ INITIALIZATION

builder.Services.AddSingleton<IConnection>(sp =>
{
    var logger = sp.GetRequiredService<ILogger<Program>>();

    var host = Environment.GetEnvironmentVariable("RABBITMQ_HOST");
    var user = Environment.GetEnvironmentVariable("RABBITMQ_USERNAME");
    var pass = Environment.GetEnvironmentVariable("RABBITMQ_PASSWORD");
    var portStr = Environment.GetEnvironmentVariable("RABBITMQ_PORT") ?? "5672";

    logger.LogInformation("RabbitMQ host: {Host}, port: {Port}, user: {User}", host, portStr, user);

    var factory = new ConnectionFactory
    {
        HostName = host,
        UserName = user,
        Password = pass,
        Port = int.Parse(portStr),
        DispatchConsumersAsync = true
    };
    return factory.CreateConnection();
});

builder.Services.AddHostedService<InventoryMockResponder>(); // RABBITMQ LISTENERS
builder.Services.AddHostedService<UserInformationResponsesConsumer>();
builder.Services.AddHostedService<InventoryItemsReservationResponse>();


builder.Services.AddSingleton<RabbitMqService>();

// GRAPHQL INITIALIZATION

builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>();

builder.Services.AddScoped<OrderService>();

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyHeader()
              .AllowAnyMethod();
    });
});

var app = builder.Build();

await MongoSeeder.SeedDataAsync(
    app.Services.GetRequiredService<IMongoDatabase>(),
    app.Services.GetRequiredService<ILogger<Program>>()
);

// Middleware to log requests
app.Use(async (context, next) =>
{
    var logger = context.RequestServices.GetRequiredService<ILogger<Program>>();
    logger.LogInformation("Incoming request: {Method} {Path}", context.Request.Method, context.Request.Path);
    await next.Invoke();
    logger.LogInformation("Response status: {StatusCode}", context.Response.StatusCode);
});

app.UseCors();
app.UseAuthorization();

app.MapGet("/", () =>
{
    var logger = app.Logger;
    logger.LogInformation("Root endpoint called");
    return "Order Service is running";
});

// Health check endpoint
app.MapGet("/health", () => Results.Ok("Healthy"));

// Endpoint to dump some environment info for debugging
app.MapGet("/env", () =>
{
    return new
    {
        MongoDBConnectionString = Environment.GetEnvironmentVariable("MONGODB_CONNECTION_STRING"),
        MongoDBDatabaseName = Environment.GetEnvironmentVariable("MONGODB_DATABASE_NAME"),
        RabbitMQHost = Environment.GetEnvironmentVariable("RABBITMQ_HOST"),
        RabbitMQPort = Environment.GetEnvironmentVariable("RABBITMQ_PORT"),
        RabbitMQUsername = Environment.GetEnvironmentVariable("RABBITMQ_USERNAME")
    };
});

//app.UseHttpsRedirection();

app.MapGraphQL();

app.Run();