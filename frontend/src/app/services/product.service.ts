import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import {
  ProductInfo,
  Product,
  CreateProductRequest,
  UpdateProductRequest,
} from "../models/product.model";
import { environment } from "../../environments/environement";

@Injectable({
  providedIn: "root",
})
export class ProductService {
  private readonly API_URL = `${environment.apiGateway}/products`;
  userId = "";
  private http = inject(HttpClient);

  getAllProducts(): Observable<ProductInfo[]> {
    return this.http.get<ProductInfo[]>(this.API_URL);
  }


  getSellerProducts(sellerId: string | null): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API_URL}/all/${sellerId}`);
  }

  createProduct(product: CreateProductRequest,sellerId: string): Observable<Product> {
    return this.http.post<Product>(`${this.API_URL}/${sellerId}`, product);
  }

  updateProduct(
    producId: string,
    product: UpdateProductRequest
  ): Observable<Product> {
    return this.http.put<Product>(`${this.API_URL}/${producId}`, product);
  }

  deleteProduct(producId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${producId}`);
  }
}
