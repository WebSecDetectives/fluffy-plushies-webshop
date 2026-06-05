import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ItemService } from '../items/item.service';
import { Item } from '../items/item.model';

@Component({
  selector: 'app-item-detail',
  imports: [],
  templateUrl: './item-detail.html',
  styleUrls: ['./item-detail.css']
})
export class ItemDetail {
  private route = inject(ActivatedRoute);
  private itemService = inject(ItemService);

  readonly item = signal<Item | null>(null);
  readonly notFound = signal(false);

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.notFound.set(true);
      return;
    }
    this.itemService.getItem(id).subscribe({
      next: item => this.item.set(item),
      // Generic message on purpose: the backend answers 404 both for missing
      // items and for private items the caller may not see
      error: () => this.notFound.set(true)
    });
  }

  useFallbackImage(event: Event): void {
    (event.target as HTMLImageElement).src = '/toy-805814_1920.jpg';
  }
}
