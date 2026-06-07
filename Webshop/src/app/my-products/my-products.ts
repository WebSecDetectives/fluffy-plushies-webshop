import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ItemService } from '../items/item.service';
import { Item } from '../items/item.model';
import { ItemImage } from '../items/item-image/item-image';

@Component({
  selector: 'app-my-products',
  imports: [RouterLink, ItemImage],
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

}
