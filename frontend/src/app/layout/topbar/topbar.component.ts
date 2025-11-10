import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-topbar',
  imports: [CommonModule],
  templateUrl: './topbar.component.html'
})
export class TopbarComponent implements OnInit {
  isDarkMode = false;
  currentUser: User | null = null;

  constructor(
    private themeService: ThemeService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.themeService.darkMode$.subscribe(isDark => {
      this.isDarkMode = isDark;
    });

    this.authService.currentUser$.subscribe(user => {
      console.log(user);
      
      this.currentUser = user;
    });
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
