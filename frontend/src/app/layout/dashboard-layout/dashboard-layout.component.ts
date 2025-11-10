import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { TopbarComponent } from '../topbar/topbar.component';
import { SidebarService } from '../../services/sidebar.service';

@Component({
  selector: 'app-dashboard-layout',
  imports: [CommonModule, RouterOutlet, SidebarComponent, TopbarComponent],
  templateUrl: './dashboard-layout.component.html'
})
export class DashboardLayoutComponent {
  isSidebarOpen = true;

  private readonly sidebarService  = inject(SidebarService);

  ngOnInit(): void {
    this.sidebarService.isOpen$.subscribe(isOpen => {
      this.isSidebarOpen = isOpen;
    });
  }
}
