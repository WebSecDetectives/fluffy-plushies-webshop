import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, map, of, switchMap } from 'rxjs';
import { ItemService } from '../items/item.service';
import { Item, ItemRequest } from '../items/item.model';
import { ItemImage } from '../items/item-image/item-image';
import { NoticeService } from '../notice/notice.service';

// Client-side pre-checks mirroring the backend limits (UX only; the backend
// magic-byte/size validation remains the real gate).
const MAX_IMAGE_BYTES = 2 * 1024 * 1024;
const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png'];
// Same signatures the backend allowlists; catches renamed files before a round-trip.
const JPEG_MAGIC = [0xff, 0xd8, 0xff];
const PNG_MAGIC = [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a];

const VALIDATION_MESSAGES: Record<string, string> = {
  name: 'Name is required.',
  price: 'Price must be 0 or more.',
  stock: 'Stock must be 0 or more.',
  description: 'Description is required.',
  ageGroup: 'Age group is required.',
  itemType: 'Type is required.',
  material: 'Material is required.'
};

/**
 * Shared form for creating ("/my-products/new") and editing ("/items/:id/edit")
 * a product. Edit mode is active when the route carries an item id. The image is
 * optional and uploaded separately after the item is saved.
 */
@Component({
  selector: 'app-product-form',
  imports: [ReactiveFormsModule, ItemImage],
  templateUrl: './product-form.html',
  styleUrls: ['./product-form.css']
})
export class ProductForm {
  private fb = inject(FormBuilder);
  private itemService = inject(ItemService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);
  private noticeService = inject(NoticeService);

  readonly itemId = this.route.snapshot.paramMap.get('id');
  readonly isEdit = this.itemId !== null;

  readonly errorMessage = signal<string | null>(null);
  readonly submitting = signal(false);

  readonly selectedFile = signal<File | null>(null);
  readonly fileError = signal<string | null>(null);
  readonly previewUrl = signal<string | null>(null);

  productForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    price: [null, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    visibility: ['PUBLIC'],
    description: ['', Validators.required],
    ageGroup: ['', Validators.required],
    itemType: ['', Validators.required],
    material: ['', Validators.required]
  });

  constructor() {
    if (this.itemId) {
      this.itemService.getItem(this.itemId).subscribe({
        next: item => this.prefill(item),
        error: () => this.errorMessage.set('Could not load the product.')
      });
    }
    this.destroyRef.onDestroy(() => this.revokePreview());
  }

  errorFor(controlName: string): string | null {
    const control = this.productForm.get(controlName);
    if (control && control.invalid && (control.dirty || control.touched)) {
      return VALIDATION_MESSAGES[controlName];
    }
    return null;
  }

  async onFileChange(event: Event): Promise<void> {
    const fileInput = event.target as HTMLInputElement;
    const file = fileInput.files?.[0] ?? null;
    this.fileError.set(null);

    if (file && !(await this.isValidImageFile(file))) {
      fileInput.value = '';
      this.setSelectedFile(null);
      return;
    }
    this.setSelectedFile(file);
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
        material: v.material
      }
    };

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.noticeService.clear();

    const save$ = this.itemId
      ? this.itemService.updateItem(this.itemId, request)
      : this.itemService.createItem(request);

    // The item is saved first; the image upload is a separate request. If only the
    // upload fails the item still exists, so navigate and surface the image error.
    save$.pipe(
      switchMap(item => this.uploadImageIfChosen(item))
    ).subscribe({
      next: item => this.router.navigate(['/items', item.id]),
      error: () => {
        this.errorMessage.set('Could not save the product. Please try again.');
        this.submitting.set(false);
      }
    });
  }

  private uploadImageIfChosen(item: Item) {
    const file = this.selectedFile();
    if (!file) {
      return of(item);
    }
    return this.itemService.uploadImage(item.id, file).pipe(
      map(() => item),
      catchError(error => {
        // App-level notice: the form navigates away, so a form-local signal wouldn't survive.
        this.noticeService.show('Product saved, but the image could not be uploaded: ' + this.imageErrorMessage(error));
        return of(item);
      })
    );
  }

  private imageErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 413) {
        return 'Image is larger than 2 MB.';
      }
      if (typeof error.error?.message === 'string') {
        return error.error.message;
      }
    }
    return 'Please try again from the edit page.';
  }

  private async isValidImageFile(file: File): Promise<boolean> {
    if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
      this.fileError.set('Please choose a JPEG or PNG image.');
      return false;
    }
    if (file.size > MAX_IMAGE_BYTES) {
      this.fileError.set('Image must be 2 MB or smaller.');
      return false;
    }
    if (!(await this.hasImageMagicBytes(file))) {
      this.fileError.set('The file content is not a valid JPEG or PNG image.');
      return false;
    }
    return true;
  }

  // The browser derives file.type from the extension, so also check the actual
  // bytes. UX only — the backend magic-byte validation remains authoritative.
  private async hasImageMagicBytes(file: File): Promise<boolean> {
    const header = new Uint8Array(await file.slice(0, PNG_MAGIC.length).arrayBuffer());
    const matches = (magic: number[]) =>
      magic.length <= header.length && magic.every((byte, i) => header[i] === byte);
    return matches(JPEG_MAGIC) || matches(PNG_MAGIC);
  }

  private setSelectedFile(file: File | null): void {
    this.revokePreview();
    this.selectedFile.set(file);
    this.previewUrl.set(file ? URL.createObjectURL(file) : null);
  }

  private revokePreview(): void {
    const url = this.previewUrl();
    if (url) {
      URL.revokeObjectURL(url);
    }
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
      material: item.details.material
    });
  }
}
