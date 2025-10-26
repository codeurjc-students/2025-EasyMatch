import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { firstValueFrom } from 'rxjs';

export const canActivateAuth: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isLoggedIn = await firstValueFrom(authService.checkAuthStatus());

  if (!isLoggedIn) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};