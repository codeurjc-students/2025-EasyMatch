import { Component, inject, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
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
  private userService = inject(UserService);

  ngOnInit(): void {
    this.loadUser();
  }

  private loadUser(): void {
    this.userService.getCurrentUser().subscribe({
      next: (data) => this.user = signal({id: data.id, realname: data.realname, username: data.username, email: data.email, birthDate: data.birthDate, gender: data.gender, description: data.description, level: data.level,
        stats: data.stats, levelHistory: data.levelHistory, roles: data.roles
      } ),
      error: (err) => console.error('Error al obtener el usuario:', err),
    });
  }
  
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

  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }
  goToProfile() {
    window.location.href = "/profile";
  }
}
