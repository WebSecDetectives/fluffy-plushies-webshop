import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';
import { getDownloadURL, ref, StorageReference, uploadBytes } from 'firebase/storage';
import { storage } from '../../environments/firebase.config';
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
  selectedFile: File | null = null;
  imgUrl: string | null = null;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
      street: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required],
      imgUrl: [null]
    });
  }

  errorFor(controlName: string): string | null {
    const control = this.registerForm.get(controlName);
    if (control && control.invalid && (control.dirty || control.touched)) {
      return VALIDATION_MESSAGES[controlName];
    }
    return null;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  async onSubmit(event: Event): Promise<void> {
    event.preventDefault();
    if (this.registerForm.invalid) {
      // Reveal per-field error messages for fields the user hasn't visited yet
      this.registerForm.markAllAsTouched();
      return;
    }

    // 1) Upload image if selected
    if (this.selectedFile) {
      const path = `user/${this.selectedFile.name}`;
      const fileRef: StorageReference = ref(storage, path);
      try {
        await uploadBytes(fileRef, this.selectedFile);
        this.imgUrl = await getDownloadURL(fileRef);
        console.log('Image uploaded: ', fileRef);
      } catch (error) {
        console.error('Upload failed:', error);
        alert('Image upload failed. Please try again.');
        return;
      }
    }

    // 2) Prepare payload including imgUrl
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
      },
      imgUrl: this.imgUrl
    };

    // 3) Call backend API
    this.http.post(this.baseUrlIdentity, payload).subscribe({
      next: () => alert('Registration successful!'),
      error: err => {
        console.error('Registration failed:', err);
        alert('Registration failed.');
      }
    });
  }
}
