import { Routes } from '@angular/router';
import { CreateAccount } from './create-account/create-account';
import { UpdateAccount } from './update-account/update-account';
import { Home } from './home/home';
import { Login } from './login/login';

export const routes: Routes = [
  {
    path: '',
    component: Home
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
