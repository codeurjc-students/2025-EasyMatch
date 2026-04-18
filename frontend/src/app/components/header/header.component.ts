import { Component, computed, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Router, RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MatDivider } from "@angular/material/divider";
import { LoginService } from '../../service/login.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { UserService } from '../../service/user.service';
import { GlobalSportState } from '../../global-sport-state';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, RouterModule, MatMenuModule, MatDivider],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private apiUrl = environment.apiUrl;

  private sportState = inject(GlobalSportState);
  private loginService = inject(LoginService);
  private router = inject(Router);
  user = toSignal(this.loginService.currentUser$, { initialValue: null });
  currentLevel = this.sportState.level;
  isAdmin = toSignal(this.loginService.isAdmin$, { initialValue: false });

  logout() {
    this.loginService.logout().subscribe({
      next: () => {
        this.router.navigate(['/matches']);
      },
      error: err => console.error(err)
    });
  }

  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }
  
  getSportName(): string | undefined{
    return this.sportState.sportProfile()?.sportName;
  }

  getSportIcon(sportName: string): string {
    switch (sportName.toLowerCase()) {
      case 'futbol':
        return 'sports_soccer';
      case 'baloncesto':
        return 'sports_basketball';
      case 'tenis':
        return 'sports_tennis';
      case 'padel':
        return 'sports_tennis';
      case 'voleibol':
        return 'sports_volleyball';
      default:
        return 'sports';
    }
  }

  goToAdminPanel(): void {
    this.router.navigate(['/admin']);
  }
}
