import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { ProductService } from "../../services/product.service";
import { AuthService } from "../../services/auth.service";
import { ThemeService } from "../../services/theme.service";
import { Product, ProductInfo, SellerInfo } from "../../models/product.model";
import { User } from "../../models/user.model";
import { DecodedToken } from "../../models/jwt.model";
import { jwtDecode } from "jwt-decode";
@Component({
  selector: "app-products-list",
  imports: [CommonModule],
  templateUrl: "./products-list.component.html",
})
export class ProductsListComponent implements OnInit {
  productInfos: ProductInfo[] = [];
  loading = true;
  currentUser: User | null = null;
  isDarkMode = false;

  private productService = inject(ProductService);
  private authService = inject(AuthService);
  private themeService = inject(ThemeService);
  private router = inject(Router);

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
    });

    this.themeService.darkMode$.subscribe((isDark) => {
      this.isDarkMode = isDark;
    });

    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllProducts().subscribe({
      next: (ProductInfo) => {
        console.log(ProductInfo);

        this.productInfos = ProductInfo;
        console.log(this.productInfos[0].medias[0]);

        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        err.status == 401 ? this.authService.logout() : null;
      },
    });
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]);
  }

  navigateToDashboard(): void {
    this.router.navigate(["/dashboard"]);
  }
}
