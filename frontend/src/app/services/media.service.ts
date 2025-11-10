import { inject, Injectable } from "@angular/core";
import { HttpClient, HttpEvent } from "@angular/common/http";
import { BehaviorSubject, concatMap, from, Observable, switchMap } from "rxjs";
import {
  Media,
  MediaRequeste,
  UploadMediaResponse,
} from "../models/media.model";
import { environment } from "../../environments/environement";
import { Product } from "../models/product.model";

@Injectable({
  providedIn: "root",
})
export class MediaService {
  private readonly API_URL = `${environment.apiGateway}/medias`;

  private http = inject(HttpClient);

  getProductMedia(productId: string): Observable<Media[]> {
    return this.http.get<Media[]>(`${this.API_URL}/product/${productId}`);
  }

  uploadMedia(productId: string, files: File[]): Observable<any> {
    // from(files) => transforme ton tableau de fichiers en flux RxJS
    return from(files).pipe(
      concatMap((file) => {
        // 1️⃣ Upload du fichier sur Cloudinary
        const formData = new FormData();
        formData.append("file", file);
        formData.append("upload_preset", environment.cloudinary.mediaPreset);

        return this.http
          .post<any>(environment.cloudinary.apiCloudinary, formData)
          .pipe(
            // 2️⃣ Quand l’upload réussit, on enregistre dans le backend
            switchMap((res) => {
              console.log(res);
              const secureUrl = res.secure_url;
              const publicId = res.public_id;
              const media: MediaRequeste = {
                imagePath: secureUrl,
                fileName: file.name,
                cloudId: publicId,
                fileSize: file.size,
                mimeType: file.type,
                productId: productId,
                uploadDate: new Date().toISOString(),
              };

              return this.http.post<any>(`${this.API_URL}/${productId}`, media);
            })
          );
      })
    );
  }

  deleteMedia(id: string, cloudId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}/${cloudId}`);
  }

  validateFile(file: File): { valid: boolean; error?: string } {
    const maxSize = 2 * 1024 * 1024;
    const allowedTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];

    if (!allowedTypes.includes(file.type)) {
      return {
        valid: false,
        error: "Only image files are allowed (JPEG, PNG, GIF, WebP)",
      };
    }

    if (file.size > maxSize) {
      return { valid: false, error: "File size must be less than 2MB" };
    }

    return { valid: true };
  }
}
