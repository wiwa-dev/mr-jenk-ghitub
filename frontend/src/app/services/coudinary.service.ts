import { inject, Injectable } from "@angular/core";
import { environment } from "../../environments/environement";
import { firstValueFrom } from "rxjs";
import { HttpClient } from "@angular/common/http";


Injectable({
    providedIn:'root'
})

export class CloudinaryService{

    private readonly http = inject(HttpClient);

    
    UploadImage(files:File){
        const formData = new FormData();
        formData.append("file", this.selectedFile);
        formData.append("upload_preset", environment.cloudinary.uploadPreset);

        try {
          const res: any = await firstValueFrom(
            this.http.post(`${environment.cloudinary.apiCloudinary}`, formData)
          );

          this.cloudinaryResponse = res;
          this.avatar = this.cloudinaryResponse.secure_url;
          console.log("Upload réussi ✅", res);
          this.submitButton = "Creating Account...";
        } catch (err) {
          console.error("Erreur upload ❌", err);
          this.error = "Error File Upload ❌";
          this.submitButton = "Creating Account...";
        }
      }
    }
}