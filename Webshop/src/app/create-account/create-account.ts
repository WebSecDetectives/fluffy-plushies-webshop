import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { getAuth, onAuthStateChanged } from 'firebase/auth';
import { environment } from '../../enviroments/enviroment';
import { getDownloadURL, ref, StorageReference, uploadBytes } from 'firebase/storage';
import { storage } from '../../enviroments/firebase.config';

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
  imageUrl: string | null = null;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      password: ['', Validators.required],
      street: ['', Validators.required],
      postal_code: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required],
      image_url: [null]
    });

    // Optional: Redirect if not authenticated
    const auth = getAuth();
    onAuthStateChanged(auth, user => {
      if (!user) {
        // Handle unauthenticated state (e.g., navigate to login)
        console.warn('User not authenticated. Redirecting to login.');
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  async onSubmit(event: Event): Promise<void> {
    event.preventDefault();
    if (this.registerForm.invalid) {
      alert('Please fill in all required fields.');
      return;
    }

    // 1) Upload image if selected
    if (this.selectedFile) {
      const path = `user/${this.selectedFile.name}`;
      const fileRef: StorageReference = ref(storage, path);
      try {
        await uploadBytes(fileRef, this.selectedFile);
        this.imageUrl = await getDownloadURL(fileRef);
        console.log('Image uploaded: ', fileRef);
      } catch (error) {
        console.error('Upload failed:', error);
        alert('Image upload failed. Please try again.');
        return;
      }
    }

    // 2) Prepare payload including imageUrl
    const v = this.registerForm.value;
    const payload = {
      username: v.username,
      email: v.email,
      phone: v.phone,
      password: v.password,
      address: {
        street: v.street,
        postal_code: v.postal_code,
        city: v.city,
        country: v.country
      },
      image_url: this.imageUrl
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
