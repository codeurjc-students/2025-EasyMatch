import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterModule } from '@angular/router';
import { User } from '../../models/user.model';
import { UserService } from '../../service/user.service';
import { environment } from '../../../environments/environment';
import { MatDivider } from "@angular/material/divider";
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, RouterModule, MatMenuModule, MatDivider],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private userService = inject(UserService);
  user = signal<User | null>(null);
  private apiUrl = environment.apiUrl;
  private loginService = inject(LoginService);

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
}
