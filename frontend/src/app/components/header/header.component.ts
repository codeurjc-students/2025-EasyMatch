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
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, RouterModule, MatMenuModule, MatDivider],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private loginService = inject(LoginService);
  user = toSignal(this.loginService.currentUser$, { initialValue: null });
  private apiUrl = environment.apiUrl;



  logout() {
    this.loginService.logout().subscribe({
      next: () => {

        sessionStorage.removeItem("token");
        sessionStorage.removeItem("authorities");

        window.location.href = "/matches";
      },
      error: err => console.error(err)
    });
  }

  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }
}
