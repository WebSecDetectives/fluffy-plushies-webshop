# config/mongo.rb
require "mongo"

Mongo::Logger.logger.level = ::Logger::FATAL # Quiet logging

mongo_url = ENV["MONGO_URL"] || "mongodb://localhost:27017/flushy_inventory"
DB = Mongo::Client.new(mongo_url)
