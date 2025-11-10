import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environement';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

   // 🔹 Liste des URLs à ignorer (ex: Cloudinary)
   const excludedUrls = [
    environment.cloudinary.apiCloudinary,
    `${environment.apiGateway}/users/auth/register`,
    `${environment.apiGateway}/users/auth/login`,
    `${environment.apiGateway}/products` // ex: "https://api.cloudinary.com/v1_1/dkjehxae7/image/upload"
  ];

  // 🔹 Vérifie si la requête correspond à une URL exclue
  const isExcluded = excludedUrls.some(url => req.url === url);

  if (!isExcluded && token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  return next(req);
};
