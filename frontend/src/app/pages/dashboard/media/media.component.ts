import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MediaService } from "../../../services/media.service";
import { Media } from "../../../models/media.model";
import { ProductService } from "../../../services/product.service";
import { AuthService } from "../../../services/auth.service";
import { Product } from "../../../models/product.model";

@Component({
  selector: "app-media",
  imports: [CommonModule],
  templateUrl: "./media.component.html",
})
export class MediaComponent extends ProductService implements OnInit {
  mediaList: Media[] = [];
  loading = true;
  selectedMedia: Media | null = null;
  showPreviewModal = false;
  products: Product[] = [];
  // private productService = inject(ProductService);
  private mediaService = inject(MediaService);
  private authService = inject(AuthService);
  ngOnInit(): void {
    this.authService.currentPayload$.subscribe((payload) => {
      if (payload) {
        this.userId = payload.id;
      }
    });
    this.loadMedia();
  }

  loadMedia(): void {
    this.loading = true;
    this.getSellerProducts(this.userId).subscribe({
      next: (products) => {
        console.log(products);
        this.products = products;
        this.mediaList = this.getMediaFromUser();
        console.log(this.mediaList);

        this.loading = false;
      },
      error: (err) => {
        err.status == 401 ? this.authService.logout() : null;
        this.loading = false;
      },
    });
  }
  getMediaFromUser(): Media[] {
    return this.products
      .flatMap((p) => p.medias)
      .sort(
        (a, b) =>
          new Date(b.uploadDate).getTime() - new Date(a.uploadDate).getTime()
      );
  }
  openPreview(media: Media): void {
    this.selectedMedia = media;
    this.showPreviewModal = true;
  }

  closePreview(): void {
    this.showPreviewModal = false;
    this.selectedMedia = null;
  }

  deleteMedia(media: Media): void {
    if (confirm(`Are you sure you want to delete "${media.imagePath}"?`)) {
      this.mediaService.deleteMedia(media.id, media.cloudId).subscribe({
        next: () => {
          this.loadMedia();
        },
      });
    }
  }

  getFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1048576) return (bytes / 1024).toFixed(2) + " KB";
    return (bytes / 1048576).toFixed(2) + " MB";
  }
}
