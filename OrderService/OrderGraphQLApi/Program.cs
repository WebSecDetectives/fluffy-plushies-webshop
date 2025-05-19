using MongoDB.Driver;
//using repositories.orderRepository;
using OrderGraphQLApi.graphql.mutations;
using OrderGraphQLApi.services;
using RabbitMQ.Client;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// builder.Services.AddEndpointsApiExplorer(); // 🔸 Only needed for Swagger UI
// builder.Services.AddSwaggerGen();           // 🔸 Only needed for Swagger

// builder.Services.AddControllers();          // 🔸 Only needed for REST controllers

builder.Services.AddAuthorization();

builder.Services.AddSingleton<IMongoClient>(sp =>
{
    var config = sp.GetRequiredService<IConfiguration>();
    var connectionString = config.GetSection("MongoDbSettings:ConnectionString").Value;
    return new MongoClient(connectionString);
});

builder.Services.AddScoped(sp =>
{
    var config = sp.GetRequiredService<IConfiguration>();
    var client = sp.GetRequiredService<IMongoClient>();
    var databaseName = config.GetSection("MongoDbSettings:DatabaseName").Value;
    return client.GetDatabase(databaseName); // <-- this resolves IMongoDatabase
});

builder.Services.AddSingleton<IConnectionFactory>(_ => new ConnectionFactory
{
    HostName = "localhost",        // or your RabbitMQ container name if using Docker Compose
    UserName = "guest",
    Password = "guest",
    DispatchConsumersAsync = true // enables async consumers (optional, good practice)
});


builder.Services.AddSingleton<RabbitMqService>();

builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>();

//builder.Services.AddScoped<OrdersRepository>();
builder.Services.AddScoped<OrderService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
// if (app.Environment.IsDevelopment())
// {
//     app.UseSwagger();                      // 🔸 Only needed for Swagger UI
//     app.UseSwaggerUI();                   // 🔸 Only needed for Swagger UI
// }

app.UseHttpsRedirection();
app.UseAuthorization();

// app.MapControllers();                      // 🔸 Only needed for REST

app.MapGraphQL();                             // ✅ GraphQL endpoint

app.Run();