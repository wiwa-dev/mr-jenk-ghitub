import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  FormControl,
} from "@angular/forms";
import { ProductService } from "../../../services/product.service";
import { MediaService } from "../../../services/media.service";
import {
  CreateProductRequest,
  Product,
  UpdateProductRequest,
} from "../../../models/product.model";
import { AuthService } from "../../../services/auth.service";
import {} from "@angular/forms";
import { ErrorService } from "../../../services/error.service";
import { BehaviorSubject } from "rxjs";
import { Media } from "../../../models/media.model";

@Component({
  selector: "app-products",
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./products.component.html",
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  medias: Media[] = [];
  previewUrls: string[] = [];
  error = "";
  succes: string | null = null;
  showModal = false;
  showUploadModal = false;
  loadingUpload = false;
  isEditMode = false;
  selectedProduct: Product | null = null;
  selectedFiles: File[] = [];
  uploadError = "";
  currentProductForUpload: Product | null = null;
  userId!: string;

  private productService = inject(ProductService);
  private mediaService = inject(MediaService);
  private authService = inject(AuthService);
  protected errorService = inject(ErrorService);

  protected readonly form = new FormGroup({
    name: new FormControl("", [Validators.required]),
    description: new FormControl("", [Validators.required]),
    price: new FormControl(0, [Validators.required, Validators.min(0)]),
    quantity: new FormControl(0, [Validators.required, Validators.min(0)]),
  });

  ngOnInit(): void {
    this.authService.currentPayload$.subscribe((payload) => {
      if (payload) {
        this.userId = payload.id;
      }
    });

    this.loadProducts(this.userId);
  }

  loadProducts(userId: string | null): void {
    this.loading = true;
    this.productService.getSellerProducts(userId).subscribe({
      next: (products) => {
        console.log(products);

        this.products = products;
        this.loading = false;
      },
      error: (err) => {
        console.log(err);     
        this.loading = false;
        err.status == 401 ? this.authService.logout() : null;
      },
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.form?.reset({ price: 0, quantity: 0 });
    this.showModal = true;
  }

  openEditModal(product: Product): void {
    this.isEditMode = true;
    this.selectedProduct = product;
    this.form.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      quantity: product.quantity,
    });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedProduct = null;
    this.error = "";
    this.succes = "";
    this.form.reset();
  }

  onSubmit(): void {
    if (this.form.valid) {
      if (this.isEditMode && this.selectedProduct) {
        this.productService
          .updateProduct(
            this.selectedProduct.id,
            this.form.value as UpdateProductRequest
          )
          .subscribe({
            next: () => {
              this.loadProducts(this.userId);
              this.closeModal();
            },
            error:(err)=>{
              err.status == 401 ? this.authService.logout() : null;
            }
          });
      } else {
        this.productService
          .createProduct(this.form.value, this.userId)
          .subscribe({
            next: () => {
              this.loadProducts(this.userId);
              this.error = "";
              this.succes = "Successfully Create Product ✅";
              setTimeout(() => {
                this.closeModal();
              }, 3000);
            },
            error:(err)=>{
              err.status == 401 ? this.authService.logout() : null;
              console.log(err);
              this.error = this.errorService.getErrorFromStatus(err);
            },
          });
      }
    } else {
      Object.keys(this.form.controls).forEach((key) => {
        this.form.get(key)?.markAsTouched();
      });
    }
  }

  deleteProduct(product: Product): void {
    if (confirm(`Are you sure you want to delete "${product.name}"?`)) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          this.loadProducts(this.userId);
        },
      });
    }
  }

  openUploadModal(product: Product): void {
    this.currentProductForUpload = product;
    this.selectedFiles = [];
    this.uploadError = "";
    this.showUploadModal = true;
  }

  closeUploadModal(): void {
    this.showUploadModal = false;
    this.currentProductForUpload = null;
    this.selectedFiles = [];
    this.previewUrls = [];
    this.uploadError = "";
  }

  onFilesSelected(event: any): void {
    const files = Array.from(event.target.files) as File[];
    this.uploadError = "";
    this.previewUrls = [];
    for (const file of files) {
      const validation = this.mediaService.validateFile(file);
      if (!validation.valid) {
        this.uploadError = validation.error || "Invalid file";
        return;
      }
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrls.push(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
    console.log(this.previewUrls);
    this.selectedFiles = files;
  }
  removeAvatar(url: string): void {
    const index = this.previewUrls.indexOf(url);
    if (index !== -1) {
      this.previewUrls.splice(index, 1);
      this.selectedFiles.splice(index, 1);
    }
  }
  uploadMedia(): void {
    if (this.selectedFiles.length > 0 && this.currentProductForUpload) {
      console.log(
        "Début du téléversement pour le produit",
        this.currentProductForUpload.id
      );
      this.loadingUpload = true;
      this.mediaService
        .uploadMedia(this.currentProductForUpload.id, this.selectedFiles)
        .subscribe({
          next: (response) => {
            console.log("✅ Fichier enregistré avec succès :", response);
          },
          error:(err)=>{
            err.status == 401 ? this.authService.logout() : null;
            console.log("❌ Erreur pendant upload/enregistrement :", err);
            this.uploadError = err.error?.message || "Upload failed";
            this.loadingUpload = false
          },
          complete: () => {
            this.closeUploadModal();
            this.loadProducts(this.userId);
            this.loadingUpload = false;
            console.log("🎉 Tous les fichiers ont été traités");
          },
        });
    }
  }
  getFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1048576) return (bytes / 1024).toFixed(2) + " KB";
    return (bytes / 1048576).toFixed(2) + " MB";
  }
  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
