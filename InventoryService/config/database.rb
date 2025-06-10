require 'sequel'
require 'dotenv/load'

DB = Sequel.connect(
  adapter:  'mysql2',
  host:     ENV['INVENTORY_DB_HOST'],
  port:     ENV['INVENTORY_DB_PORT'],
  database: ENV['INVENTORY_DB_NAME'],
  user:     ENV['INVENTORY_DB_USER'],
  password: ENV['INVENTORY_DB_PASSWORD']
)
