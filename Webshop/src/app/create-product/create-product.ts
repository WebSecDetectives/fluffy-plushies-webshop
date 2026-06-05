import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ItemService } from '../items/item.service';
import { ItemRequest } from '../items/item.model';

// https URL, max 2048 chars
const IMG_URL_PATTERN = /^https:\/\/\S{1,2040}$/;

const VALIDATION_MESSAGES: Record<string, string> = {
  name: 'Name is required.',
  price: 'Price must be 0 or more.',
  stock: 'Stock must be 0 or more.',
  description: 'Description is required.',
  ageGroup: 'Age group is required.',
  itemType: 'Type is required.',
  material: 'Material is required.',
  imgUrl: 'Image URL must be a valid https link.'
};

@Component({
  selector: 'app-create-product',
  imports: [ReactiveFormsModule],
  templateUrl: './create-product.html',
  styleUrls: ['./create-product.css']
})
export class CreateProduct {
  private fb = inject(FormBuilder);
  private itemService = inject(ItemService);
  private router = inject(Router);

  readonly errorMessage = signal<string | null>(null);
  readonly submitting = signal(false);

  productForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    price: [null, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    visibility: ['PUBLIC'],
    description: ['', Validators.required],
    ageGroup: ['', Validators.required],
    itemType: ['', Validators.required],
    material: ['', Validators.required],
    imgUrl: ['', [Validators.required, Validators.pattern(IMG_URL_PATTERN)]]
  });

  errorFor(controlName: string): string | null {
    const control = this.productForm.get(controlName);
    if (control && control.invalid && (control.dirty || control.touched)) {
      return VALIDATION_MESSAGES[controlName];
    }
    return null;
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const v = this.productForm.value;
    const request: ItemRequest = {
      name: v.name,
      price: v.price,
      stock: v.stock,
      visibility: v.visibility,
      details: {
        description: v.description,
        ageGroup: v.ageGroup,
        itemType: v.itemType,
        material: v.material,
        imgUrl: v.imgUrl
      }
    };

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.itemService.createItem(request).subscribe({
      next: item => this.router.navigate(['/items', item.id]),
      error: () => {
        this.errorMessage.set('Could not create the product. Please try again.');
        this.submitting.set(false);
      }
    });
  }
}
