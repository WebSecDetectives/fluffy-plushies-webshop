import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'Fluffy Plushies';
  loggedIn: boolean = false;

  constructor(private router: Router) {
  }
  login() {
    this.loggedIn = true;
  }

  logout() {
    this.loggedIn = false;
  }

  home() {
    console.log('Navigating to home');
    this.router.navigate(['/']);
  }
}
