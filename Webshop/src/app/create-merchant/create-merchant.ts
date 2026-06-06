import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
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

/** Admin-only: creates a MERCHANT account via POST /users/merchant */
@Component({
  selector: 'app-create-merchant',
  imports: [ReactiveFormsModule],
  templateUrl: './create-merchant.html',
  styleUrls: ['./create-merchant.css']
})
export class CreateMerchant {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);

  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);
  readonly submitting = signal(false);

  merchantForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
    password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
    street: ['', Validators.required],
    postalCode: ['', Validators.required],
    city: ['', Validators.required],
    country: ['', Validators.required]
  });

  errorFor(controlName: string): string | null {
    const control = this.merchantForm.get(controlName);
    if (control && control.invalid && (control.dirty || control.touched)) {
      return VALIDATION_MESSAGES[controlName];
    }
    return null;
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.merchantForm.invalid) {
      this.merchantForm.markAllAsTouched();
      return;
    }

    const v = this.merchantForm.value;
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

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.http.post(`${environment.baseUrlIdentity}/users/merchant`, payload).subscribe({
      next: () => {
        this.successMessage.set(`Merchant account "${v.username}" created.`);
        this.merchantForm.reset();
        this.submitting.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not create the merchant account.');
        this.submitting.set(false);
      }
    });
  }
}
