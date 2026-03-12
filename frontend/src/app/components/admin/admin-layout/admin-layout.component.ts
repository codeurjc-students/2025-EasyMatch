import { Component, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoginService } from '../../../service/login.service';
import { environment } from '../../../../environments/environment';
import { UserService } from '../../../service/user.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterModule, MatMenuModule, MatIconModule, MatButtonModule],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss'],
})
export class AdminLayoutComponent {

  sidebarCollapsed = false;
  private apiUrl = environment.apiUrl;
  user = signal<User | null>(null);

  private loginService = inject(LoginService);
  private router = inject(Router);


  ngOnInit(): void {
    this.loginService.currentUser$.subscribe({
      next: user => this.user.set(user),
      error: err => console.error('Error al obtener el usuario:', err)
    });
  }
  
  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  logout() {
    this.loginService.logout().subscribe({
      next: () => {
        this.router.navigate(['/'])
      },
      error: err => console.error(err)
    });
  }

  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }
  goToProfile() {
    this.router.navigate(['/profile']);
  }
}
