import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoginService } from '../../../service/login.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterModule, MatMenuModule, MatIconModule, MatButtonModule],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss'],
})
export class AdminLayoutComponent {

  sidebarCollapsed = false;

  private loginService = inject(LoginService);

  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  logout() {
    this.loginService.logout().subscribe({
      next: () => {
        window.location.href = "/login";
      },
      error: err => console.error(err)
    });
  }
}
