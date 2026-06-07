import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';
import { PASSWORD_PATTERN } from '../forms/validation-patterns';

const VALIDATION_MESSAGES: Record<string, string> = {
  username: 'Username must be between 3 and 30 characters.',
  password: 'Password must be 12-100 characters and contain an uppercase letter, a lowercase letter, a digit and a special character, without spaces.',
  email: 'Enter a valid email address.',
  phone: 'Phone number is required.',
  street: 'Street is required.',
  postalCode: 'Postal code is required.',
  city: 'City is required.',
  country: 'Country is required.'
};

@Component({
  selector: 'app-create-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-account.html',
  styleUrls: ['./create-account.css']
})
export class CreateAccount {
  baseUrlIdentity = `${environment.baseUrlIdentity}/users`;
  registerForm: FormGroup;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
      street: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required]
    });
  }

  errorFor(controlName: string): string | null {
    const control = this.registerForm.get(controlName);
    if (control && control.invalid && (control.dirty || control.touched)) {
      return VALIDATION_MESSAGES[controlName];
    }
    return null;
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.registerForm.invalid) {
      // Reveal per-field error messages for fields the user hasn't visited yet
      this.registerForm.markAllAsTouched();
      return;
    }

    const v = this.registerForm.value;
    const payload = {
      username: v.username,
      email: v.email,
      phone: v.phone,
      password: v.password,
      address: {
        street: v.street,
        postalCode: v.postalCode,
        city: v.city,
        country: v.country
      }
    };

    this.http.post(this.baseUrlIdentity, payload).subscribe({
      next: () => alert('Registration successful!'),
      error: err => {
        console.error('Registration failed:', err);
        alert('Registration failed.');
      }
    });
  }
}
