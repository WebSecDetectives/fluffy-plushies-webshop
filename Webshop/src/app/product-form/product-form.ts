import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemService } from '../items/item.service';
import { Item, ItemRequest } from '../items/item.model';

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

/**
 * Shared form for creating ("/my-products/new") and editing ("/items/:id/edit")
 * a product. Edit mode is active when the route carries an item id.
 */
@Component({
  selector: 'app-product-form',
  imports: [ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrls: ['./product-form.css']
})
export class ProductForm {
  private fb = inject(FormBuilder);
  private itemService = inject(ItemService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  private readonly itemId = this.route.snapshot.paramMap.get('id');
  readonly isEdit = this.itemId !== null;

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

  constructor() {
    if (this.itemId) {
      this.itemService.getItem(this.itemId).subscribe({
        next: item => this.prefill(item),
        error: () => this.errorMessage.set('Could not load the product.')
      });
    }
  }

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
      price: Number(v.price),
      stock: Number(v.stock),
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

    const save$ = this.itemId
      ? this.itemService.updateItem(this.itemId, request)
      : this.itemService.createItem(request);

    save$.subscribe({
      next: item => this.router.navigate(['/items', item.id]),
      error: () => {
        this.errorMessage.set('Could not save the product. Please try again.');
        this.submitting.set(false);
      }
    });
  }

  private prefill(item: Item): void {
    this.productForm.patchValue({
      name: item.name,
      price: item.price,
      stock: item.stock,
      visibility: item.visibility,
      description: item.details.description,
      ageGroup: item.details.ageGroup,
      itemType: item.details.itemType,
      material: item.details.material,
      imgUrl: item.details.imgUrl
    });
  }
}
