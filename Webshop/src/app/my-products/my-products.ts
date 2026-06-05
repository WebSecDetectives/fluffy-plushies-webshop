import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ItemService } from '../items/item.service';
import { Item } from '../items/item.model';

@Component({
  selector: 'app-my-products',
  imports: [RouterLink],
  templateUrl: './my-products.html',
  styleUrls: ['./my-products.css']
})
export class MyProducts {
  private itemService = inject(ItemService);

  readonly items = signal<Item[]>([]);
  readonly loadFailed = signal(false);

  constructor() {
    this.itemService.getMyItems().subscribe({
      next: items => this.items.set(items),
      error: () => this.loadFailed.set(true)
    });
  }

  useFallbackImage(event: Event): void {
    (event.target as HTMLImageElement).src = '/toy-805814_1920.jpg';
  }
}
