import { Media } from "./media.model";

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId: string;
  medias: Media[];
}

export interface SellerInfo {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface ProductInfo {
  product: Product;
  seller: SellerInfo;
  medias: Media[];
}


export interface CreateProductRequest {
  name?: string | null;
  description?: string | null;
  price?: number | null;
  quantity?: number | null;
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
}
