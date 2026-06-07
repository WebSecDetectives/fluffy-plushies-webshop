import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, signal } from '@angular/core';
import { ItemService } from '../item.service';

const FALLBACK_IMAGE = '/toy-805814_1920.jpg';

/**
 * Renders a product image fetched through HttpClient so the JWT is attached
 * (a plain <img src> sends no Authorization header, so private items' images
 * would 404 even for their owner). Shows the fallback plushie while loading
 * and when the backend returns 404 (no image, or item hidden from the caller).
 */
@Component({
  selector: 'app-item-image',
  changeDetection: ChangeDetectionStrategy.OnPush,
  // (error) covers decode failures the HTTP error branch can't see (200 with corrupt bytes).
  template: `<img [class]="imgClass()" [src]="src()" [alt]="alt()" (error)="src.set(fallback)">`
})
export class ItemImage {
  private itemService = inject(ItemService);
  private destroyRef = inject(DestroyRef);

  readonly itemId = input.required<string>();
  readonly alt = input('');
  readonly imgClass = input('');

  readonly src = signal(FALLBACK_IMAGE);
  protected readonly fallback = FALLBACK_IMAGE;
  private objectUrl: string | null = null;

  constructor() {
    // Refetch whenever itemId changes; cancel any in-flight request on change/destroy.
    effect(onCleanup => {
      const id = this.itemId();
      this.src.set(FALLBACK_IMAGE);
      const subscription = this.itemService.getItemImage(id).subscribe({
        // Sanity check: only display blobs that are actually images (e.g. a proxy
        // could answer 200 with an HTML error page); anything else → fallback.
        next: blob => blob.type.startsWith('image/') ? this.showBlob(blob) : this.src.set(FALLBACK_IMAGE),
        error: () => this.src.set(FALLBACK_IMAGE)
      });
      onCleanup(() => subscription.unsubscribe());
    });
    this.destroyRef.onDestroy(() => this.revokeObjectUrl());
  }

  private showBlob(blob: Blob): void {
    this.revokeObjectUrl();
    this.objectUrl = URL.createObjectURL(blob);
    this.src.set(this.objectUrl);
  }

  // Object URLs hold the blob in memory until revoked; release on replace and on destroy.
  private revokeObjectUrl(): void {
    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = null;
    }
  }
}
