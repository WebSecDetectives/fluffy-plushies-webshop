import React from 'react'
import { Routes, Route, Link } from 'react-router-dom'

import Admin from './components/admin'
import Cart from './components/cart'
import Products from './components/products'

function App() {
  return (
    <div>
      <nav style={{ display: 'flex', gap: '20px', padding: '20px', background: '#eee' }}>
        <Link to="/products">🧸 Products</Link>
        <Link to="/cart">🛒 Shopping cart</Link>
        <Link to="/admin">🔐 Admin</Link>
      </nav>

      <Routes>
        <Route path="/" element={<Products />} />
        <Route path="/products" element={<Products />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/admin" element={<Admin />} />
      </Routes>
    </div>
  )
}

export default App
