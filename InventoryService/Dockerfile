# inventory-service/Dockerfile

FROM docker.io/library/ruby:3.2

# Install dependencies
RUN apt-get update -qq && apt-get install -y build-essential

# Set working directory
WORKDIR /app

# Copy Gemfile and install gems
COPY Gemfile ./
RUN bundle install

# Copy the rest of the app
COPY . .

# Expose the port Sinatra runs on
EXPOSE 4567

# Run the app
CMD ["ruby", "app.rb"]
