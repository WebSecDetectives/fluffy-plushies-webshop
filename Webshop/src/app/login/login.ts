import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  loginForm: FormGroup;
  errorMessage = signal<string | null>(null);
  submitting = signal(false);

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.loginForm.invalid) {
      this.errorMessage.set('Please enter username and password.');
      return;
    }

    const { username, password } = this.loginForm.value;
    this.submitting.set(true);
    this.errorMessage.set(null);

    this.authService.login(username, password).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {
        // Generic message on purpose: don't reveal whether the username exists
        this.errorMessage.set('Login failed. Check your username and password.');
        this.submitting.set(false);
      }
    });
  }
}