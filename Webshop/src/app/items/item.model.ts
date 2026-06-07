export type Visibility = 'PUBLIC' | 'PRIVATE';

export interface ItemDetails {
  description: string;
  ageGroup: string;
  itemType: string;
  material: string;
}

export interface Item {
  id: string;
  name: string;
  price: number;
  stock: number;
  merchantId: string;
  visibility: Visibility;
  details: ItemDetails;
}

export interface ItemRequest {
  name: string;
  price: number;
  stock: number;
  visibility?: Visibility;
  details: ItemDetails;
}