# models/product.rb
require_relative '../config/database'

class Product < Sequel::Model
  plugin :timestamps, update_on_create: true

  def validate
    super
    errors.add(:name, 'cannot be empty') if name.nil? || name.strip.empty?
    errors.add(:price, 'must be a positive number') if price.nil? || price <= 0
    errors.add(:stock, 'must be 0 or more') if stock.nil? || stock < 0
  end
end
