import { Routes } from '@angular/router';
import { CreateAccount } from './create-account/create-account';
import { UpdateAccount } from './update-account/update-account';
import { Home } from './home/home';
import { Login } from './login/login';
import { ItemDetail } from './item-detail/item-detail';
import { MyProducts } from './my-products/my-products';
import { ProductForm } from './product-form/product-form';
import { CreateMerchant } from './create-merchant/create-merchant';
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
    // Ownership is checked server-side; the guard only filters by role for UX
    path: 'items/:id/edit',
    component: ProductForm,
    canActivate: [roleGuard('MERCHANT', 'ADMIN')]
  },
  {
    path: 'my-products',
    component: MyProducts,
    canActivate: [roleGuard('MERCHANT')]
  },
  {
    path: 'my-products/new',
    component: ProductForm,
    canActivate: [roleGuard('MERCHANT')]
  },
  {
    path: 'admin/create-merchant',
    component: CreateMerchant,
    canActivate: [roleGuard('ADMIN')]
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
