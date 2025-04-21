import Admin from './components/admin'
import Cart from './components/cart'
import Products from './components/products'
import React from 'react'
import { } from 'react'
import { Route } from 'react-router'
import { Routes } from 'react-router'
import { Link } from 'react-router'

function App() {

  return (
    <div>
      <div >
        <nav >
          <Link to='/products'>Products</Link>
          <Link to='/cart'>Shopping cart</Link>
          <Link to='/admin'>Admin</Link>
        </nav>
      </div>
      <Routes>
        <Route path='/' element={<Products />} />
        <Route path='/products' element={<Products />} />
        <Route path='/cart' element={<Cart />} />
        <Route path='/admin' element={<Admin />} />
      </Routes>
    </div>
  )
}

export default App
