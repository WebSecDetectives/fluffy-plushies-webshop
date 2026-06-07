import { Component, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ItemService } from '../items/item.service';
import { Item } from '../items/item.model';
import { AuthService } from '../auth/auth.service';
import { ItemImage } from '../items/item-image/item-image';

@Component({
  selector: 'app-home',
  imports: [RouterLink, ItemImage],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home {
  private itemService = inject(ItemService);
  private authService = inject(AuthService);

  readonly items = signal<Item[]>([]);
  readonly loadFailed = signal(false);

  constructor() {
    // Reload the list whenever the logged-in user changes (login/logout),
    // so items the new auth state may not see don't linger on screen
    effect(() => {
      this.authService.currentUser();
      this.loadItems();
    });
  }

  private loadItems(): void {
    this.loadFailed.set(false);
    this.itemService.getItems().subscribe({
      next: items => this.items.set(items),
      error: () => this.loadFailed.set(true)
    });
  }

}