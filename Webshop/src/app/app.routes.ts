import { Routes } from '@angular/router';
import { CreateAccount } from './create-account/create-account';
import { UpdateAccount } from './update-account/update-account';
import { Home } from './home/home';
import { Login } from './login/login';
import { ItemDetail } from './item-detail/item-detail';
import { MyProducts } from './my-products/my-products';
import { CreateProduct } from './create-product/create-product';
import { roleGuard } from './auth/role.guard';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'items/:id',
    component: ItemDetail
  },
  {
    path: 'my-products',
    component: MyProducts,
    canActivate: [roleGuard('MERCHANT')]
  },
  {
    path: 'my-products/new',
    component: CreateProduct,
    canActivate: [roleGuard('MERCHANT')]
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'create',
    component: CreateAccount
  },
  {
    path: 'update',
    component: UpdateAccount
  }
];
