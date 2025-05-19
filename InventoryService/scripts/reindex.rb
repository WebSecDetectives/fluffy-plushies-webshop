require 'dotenv/load'
require 'sequel'
require 'elasticsearch'

DB = Sequel.connect(ENV['DATABASE_URL'])  # 👈 picks up from .env
ES = Elasticsearch::Client.new(url: ENV['ELASTIC_URL'] || 'http://elasticsearch:9200')

products = DB[:products].all

products.each do |product|
  ES.index(
    index: 'products',
    id: product[:id],
    body: {
      name: product[:name],
      description: product[:description],
      price: product[:price],
      stock: product[:stock]
    }
  )
end

puts "✅ Reindexed #{products.length} products to Elasticsearch!"
