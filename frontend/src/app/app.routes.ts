import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { SellerGuard } from './guards/seller.guard';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ProductsListComponent } from './pages/products-list/products-list.component';
import { DashboardLayoutComponent } from './layout/dashboard-layout/dashboard-layout.component';
import { OverviewComponent } from './pages/dashboard/overview/overview.component';
import { ProductsComponent } from './pages/dashboard/products/products.component';
import { MediaComponent } from './pages/dashboard/media/media.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/products',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'products',
    component: ProductsListComponent
  },
  {
    path: 'dashboard',
    component: DashboardLayoutComponent,
    canActivate: [AuthGuard, SellerGuard],
    children: [
      {
        path: '',
        component: OverviewComponent
      },
      {
        path: 'products',
        component: ProductsComponent
      },
      {
        path: 'media',
        component: MediaComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/products'
  }
];
