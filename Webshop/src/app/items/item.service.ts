import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Item, ItemRequest } from './item.model';

@Injectable({ providedIn: 'root' })
export class ItemService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrlInventory}/items`;

  getItems(): Observable<Item[]> {
    return this.http.get<Item[]>(this.baseUrl);
  }

  getItem(id: string): Observable<Item> {
    return this.http.get<Item>(`${this.baseUrl}/${id}`);
  }

  getMyItems(): Observable<Item[]> {
    return this.http.get<Item[]>(`${this.baseUrl}/mine`);
  }

  createItem(request: ItemRequest): Observable<Item> {
    return this.http.post<Item>(this.baseUrl, request);
  }

  updateItem(id: string, request: Partial<ItemRequest>): Observable<Item> {
    return this.http.patch<Item>(`${this.baseUrl}/${id}`, request);
  }

  deleteItem(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}