require 'elasticsearch'

ES_CLIENT = Elasticsearch::Client.new(url: ENV['ELASTIC_URL'] || 'http://elasticsearch:9200')
