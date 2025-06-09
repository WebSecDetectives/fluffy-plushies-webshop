import React, { useEffect, useState } from 'react';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:4567/products')
      .then((res) => res.json())
      .then((data) => {
        console.log('✅ Received:', data);
        setProducts(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('❌ Error:', err);
        setLoading(false);
      });
  }, []);

  const filteredProducts = query
    ? products.filter((product) =>
        product.name && product.name.toLowerCase().includes(query.toLowerCase())
      )
    : products;
    console.log(filteredProducts);

  return (
    <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '2rem', marginBottom: '1rem' }}>Fluffy Plushies</h1>

      <input
        type="text"
        placeholder="Search plushies..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{ padding: '0.5rem', width: '100%', marginBottom: '1rem' }}
      />

      {loading ? (
        <p>Loading products...</p>
      ) : filteredProducts.length === 0 ? (
        <p>No products available.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {filteredProducts.map((p) => (
            <li
              key={p.id}
              style={{
                border: '1px solid #ccc',
                padding: '1rem',
                borderRadius: '8px',
                marginBottom: '1rem',
              }}
            >
              <h2>{p.name}</h2>
              <p>{p.description || 'No description provided.'}</p>
              <p>
                <strong>Price:</strong> ${p.price ? parseFloat(p.price).toFixed(2) : 'N/A'}
              </p>
              <p>
                <strong>Stock:</strong> {p.stock ?? 'N/A'}
              </p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}