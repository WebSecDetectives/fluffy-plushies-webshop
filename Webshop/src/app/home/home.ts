
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage';
import { storage } from '../../enviroments/firebase.config';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home{
imageUrl: string | null = null;

  async onFileSelected(event: Event) {
    const file = (event.target as HTMLInputElement)?.files?.[0];
    if (!file) return;

    const path = `user/${file.name}`;
    const fileRef = ref(storage, path);

    try {
      await uploadBytes(fileRef, file);
      this.imageUrl = await getDownloadURL(fileRef);
      console.log('Upload successful:', this.imageUrl);
    } catch (error) {
      console.error('Upload failed:', error);
    }
  }
}
