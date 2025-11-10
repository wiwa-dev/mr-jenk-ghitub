import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { jwtDecode } from "jwt-decode";
import { DecodedToken } from "../models/jwt.model";

import {
  LoginResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from "../models/user.model";
import { environment } from "../../environments/environement";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private readonly API_GATEWAY = environment.apiGateway;
  private readonly TOKEN_KEY = "jwt_token";
  private readonly TOKEN_EXPIRES_IN = "expires_in";
  private readonly USER_KEY = "current_user";
  private currentUserSubject = new BehaviorSubject<User | null>(
    this.getUserFromStorage()
  );
  public currentUser$ = this.currentUserSubject.asObservable();

  // ✅ Charger le payload dès l'initialisation du service
  private currentPayloadSubject = new BehaviorSubject<DecodedToken | null>(
    this.loadPayloadFromStorage()
  );
  public currentPayload$ = this.currentPayloadSubject.asObservable();

  private router = inject(Router);
  private http = inject(HttpClient);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.API_GATEWAY}/users/auth/login`, credentials)
      .pipe(
        tap((response) => {
          this.setSession(response);
          // ✅ Mettre à jour le payload après login
          this.currentPayloadSubject.next(this.loadPayloadFromResponse(response.token));
        })
      );
  }

  register(data: RegisterRequest): Observable<any> {
    return this.http.post<RegisterRequest>(
      `${this.API_GATEWAY}/users/auth/register`,
      data
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.TOKEN_EXPIRES_IN);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    this.currentPayloadSubject.next(null); // ✅ Reset le payload
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  loadPayloadFromStorage(): DecodedToken | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (token) {
      try {
        const decoded: DecodedToken = jwtDecode(token);
        console.log("Payload chargé:", decoded); // Debug
        return decoded;
      } catch (error) {
        console.error("Token invalide", error);
        return null;
      }
    }
    console.log("Aucun token trouvé"); // Debug
    return null;
  }

  loadPayloadFromResponse(token: string): DecodedToken | null {
    if (token) {
      try {
        const decoded: DecodedToken = jwtDecode(token);
        return decoded;
      } catch (error) {
        console.error("Token invalide", error);
        return null;
      }
    }
    return null;
  }
  
  // ✅ Méthode utile pour obtenir directement le sellerId
  getSellerId(): string | null {
    const payload = this.currentPayloadSubject.value;
    return payload?.id || null;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  isSeller(): boolean {
    const user = this.getCurrentUser();
    return user?.role === "SELLER";
  }

  isClient(): boolean {
    const user = this.getCurrentUser();
    return user?.role === "CLIENT";
  }

  private setSession(authResponse: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, authResponse.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(authResponse.user));
    this.currentUserSubject.next(authResponse.user);
  }

  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem(this.USER_KEY);
    const exp = localStorage.getItem(this.TOKEN_EXPIRES_IN);

    console.log("user:", userJson ? JSON.parse(userJson) : null,Date.now(),exp);
    return userJson ? JSON.parse(userJson) : null;
  }
}