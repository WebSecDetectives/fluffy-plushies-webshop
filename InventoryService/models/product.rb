require_relative '../config/database'
require_relative '../config/elasticsearch'  # 👈 make sure this line is included!

class Product < Sequel::Model
  plugin :timestamps, update_on_create: true

  def after_create
    super
    index_to_elasticsearch
  end

  def index_to_elasticsearch
    ES_CLIENT.index(
      index: 'products',
      id: id,
      body: {
        name: name,
        description: description,
        price: price,
        stock: stock
      }
    )
  end

  def validate
    super
    errors.add(:name, 'cannot be empty') if name.nil? || name.strip.empty?
    errors.add(:price, 'must be a positive number') if price.nil? || price <= 0
    errors.add(:stock, 'must be 0 or more') if stock.nil? || stock < 0
  end
end
