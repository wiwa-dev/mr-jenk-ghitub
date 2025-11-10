import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router, RouterLink, RouterLinkActive } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { User } from "../../models/user.model";
import { SidebarService } from "../../services/sidebar.service";

@Component({
  selector: "app-sidebar",
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: "./sidebar.component.html",
})
export class SidebarComponent implements OnInit {
  currentUser: User | null = null;
  isOpen = true;

  menuItems = [
    {
      label: "Dashboard",
      icon: "dashboard",
      route: "/dashboard",
      active: true,
    },
    {
      label: "Products",
      icon: "products",
      route: "/dashboard/products",
      active: false,
    },
    {
      label: "Media",
      icon: "media",
      route: "/dashboard/media",
      active: false,
    },
  ];

  private authService = inject(AuthService);
  private router = inject(Router);
  private sidebarService = inject(SidebarService);

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
    });
    this.sidebarService.isOpen$.subscribe(isOpen => {
      this.isOpen = isOpen;
    });
  }

  toggleSidebar(): void {
    this.sidebarService.toggleSidebar();
  }

  logout(): void {
    this.authService.logout();
  }
}
