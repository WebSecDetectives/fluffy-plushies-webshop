CREATE TABLE IF NOT EXISTS products (
  id CHAR(24) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2),
  stock INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT INTO products (id, name, description, price, stock, created_at, updated_at) VALUES
(1, 'Fluffy', 'Simple starter bear', 19.99, 3, '2025-04-18 18:32:08', '2025-05-21 13:32:52'),
(2, 'Flushy the Bear', 'Soft, cuddly, 100% plushy certified', 24.99, 92, '2025-04-20 09:08:16', '2025-05-21 14:02:53');