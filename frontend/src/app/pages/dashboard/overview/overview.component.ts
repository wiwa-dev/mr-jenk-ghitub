import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { ProductService } from "../../../services/product.service";
import { MediaService } from "../../../services/media.service";
import { Product } from "../../../models/product.model";
import { AuthService } from "../../../services/auth.service";
import { filter } from "rxjs";

@Component({
  selector: "app-overview",
  imports: [CommonModule, RouterLink],
  templateUrl: "./overview.component.html",
})
export class OverviewComponent implements OnInit {
  totalProducts = 0;
  totalMedia = 0;
  totalRevenue = 0;
  recentProducts: Product[] = [];
  loading = true;
  userId: string | null = null;

  private productService = inject(ProductService);
  private mediaService = inject(MediaService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    // Wait for payload to be available before loading data
    this.authService.currentPayload$
      .pipe(filter((payload) => payload !== null))
      .subscribe((payload) => {
        this.userId = payload!.id;
        console.log("userId loaded:", this.userId);
        this.loadDashboardData(this.userId);
      });
  }

  loadDashboardData(userId: string | null): void {
    if (!userId) {
      console.error("No userId available");
      this.loading = false;
      return;
    }

    this.loading = true;

    this.productService.getSellerProducts(userId).subscribe({
      next: (products) => {
        this.totalProducts = products.length;
        this.recentProducts = products.slice(0, 5);
        console.log(this.recentProducts);

        this.totalRevenue = products.reduce(
          (sum, p) => sum + p.price * p.quantity,
          0
        );
        this.totalMedia = products.reduce((sum, p) => sum + p.medias.length, 0);
        this.loading = false;
        console.log("Products loaded:", products.length);
      },
      error: (err) => {
        console.error("Error loading products:", err);
        this.loading = false;
        err.status == 401 ? this.authService.logout() : null;
      },
    });
  }
}
