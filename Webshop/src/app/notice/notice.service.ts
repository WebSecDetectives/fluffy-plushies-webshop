import { Injectable, signal } from '@angular/core';

/**
 * App-level notice shown as a banner in the root component, for messages that
 * must survive a navigation (e.g. "product saved, but the image upload failed").
 */
@Injectable({ providedIn: 'root' })
export class NoticeService {
  readonly message = signal<string | null>(null);

  show(message: string): void {
    this.message.set(message);
  }

  clear(): void {
    this.message.set(null);
  }
}
