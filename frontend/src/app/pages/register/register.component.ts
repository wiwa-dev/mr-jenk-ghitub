import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { firstValueFrom } from "rxjs";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { RegisterRequest, UserRole } from "../../models/user.model";
import { environment } from "../../../environments/environement";
import { HttpClient } from "@angular/common/http";
import { ErrorService } from "../../services/error.service";

@Component({
  selector: "app-register",
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./register.component.html",
})
export class RegisterComponent {
  loading = false;
  error = "";
  succes = "";
  submitButton = "Create Account";
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  cloudinaryResponse?: any;
  avatar = "";

  // ===== INJECTIONS =====
  private authService = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);
  protected errorService = inject(ErrorService);

  protected readonly form = new FormGroup({
    firstName: new FormControl("", [Validators.required]),
    lastName: new FormControl("", [Validators.required]),
    email: new FormControl("", [Validators.required, Validators.email]),
    password: new FormControl("", [
      Validators.required,
      Validators.minLength(6),
    ]),
    role: new FormControl("CLIENT", [Validators.required]),
  });

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      console.log(file);

      if (!file.type.startsWith("image/")) {
        this.error = "Please select an image file";
        return;
      }
      if (file.size > 2 * 1024 * 1024) {
        this.error = "Image size must be less than 2MB";
        return;
      }

      this.selectedFile = file;
      this.error = "";

      const reader = new FileReader();
      reader.onload = () => {
        // console.log(reader.result);
        this.previewUrl = reader.result as string;
        
      };

      reader.readAsDataURL(file);
    }
  }

  removeAvatar(): void {
    this.selectedFile = null;
    this.previewUrl = null;
  }

  async onSubmit(): Promise<void> {
    if (this.form.valid) {
      this.loading = true;
      this.submitButton = "Creating Account...";
      this.error = "";
      if (this.selectedFile) {
        this.submitButton = "Uploading avatar...";
        const formData = new FormData();
        formData.append("file", this.selectedFile);
        formData.append("upload_preset", environment.cloudinary.avatarPreset);

        try {
          const res: any = await firstValueFrom(
            this.http.post(
              `${environment.cloudinary.apiCloudinary}`,
              formData
            )
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
      const formValue = this.form.value;
      const registerData = {
        ...formValue,
        avatar: this.avatar,
      };
      this.authService.register(registerData).subscribe({
        next: (res) => {
          this.loading = false;
          console.log("Successfully Create Account ✅", res);
          this.succes = "Successfully Create Account ✅";
          setTimeout(() => {
            this.router.navigate(["/login"]);
          }, 2000);
        },
        error: (err) => {
          console.log(err.status);
          this.loading = false;
          this.error = this.errorService.getErrorFromStatus(err);
        },
      });
    } else {
      Object.keys(this.form.controls).forEach((key) => {
        this.form.get(key)?.markAsTouched();
      });
    }
  }

  
  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
