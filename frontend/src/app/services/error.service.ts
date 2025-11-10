import { Injectable ,inject} from "@angular/core";
import { FormGroup } from "@angular/forms";
import { Router } from "@angular/router";

@Injectable({ providedIn: "root" })
export class ErrorService {

  private router  = inject(Router);

  getErrorFromStatus(err: any): string {
    switch (err.status) {
      case 400:
        return "Bad request: please enter a valid informations";
      case 401:
        
        return "You would be connected for acces at this ressources";
      case 403:
        return "You do not have the necessary permissions";
      case 404:
        return "Not Found";
      case 405:
        return "Mathod Not Allow";
      case 409:
        return "User already exist, please try again";
      default:
        return "Error, please try again or contact admin";
    }
  }

  getErrorMessage(form:FormGroup,field: string): string {
    const control = form.get(field);
    if (control?.hasError("required")) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
    }
    if (control?.hasError("email")) {
      return "Please enter a valid email address";
    }
    if (control?.hasError("minlength")) {
      return "Password must be at least 6 characters";
    }
    if (control?.hasError("min")) {
        return `${field.charAt(0).toUpperCase() + field.slice(1)} must be zero or positive`;
      }
    return "";
  }
  hasError(form: FormGroup,field: string): boolean {
    const control = form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
