import { Routes } from '@angular/router';
import { CreateAccount } from './create-account/create-account';
import { UpdateAccount } from './update-account/update-account';
import { Home } from './home/home';

export const routes: Routes = [
  {
    path: '',
    component: Home
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
