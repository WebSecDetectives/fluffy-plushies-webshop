# models/product.rb
require_relative "../config/mongo"

class Product
  def self.collection
    DB[:products]
  end

  def self.all
    collection.find.map { |doc| doc.transform_keys(&:to_s) }
  end

  def self.create(data)
    result = collection.insert_one(data)
    collection.find(_id: result.inserted_id).first
  end
end
