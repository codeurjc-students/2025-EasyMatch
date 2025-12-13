import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { firstValueFrom } from 'rxjs';

export const canActivateAuth: CanActivateFn = async () => {
  const authService = inject(AuthService);

  try {
    const isLoggedIn = await firstValueFrom(authService.checkAuthStatus());
    return isLoggedIn;
  } catch {
    return false;
  }
};
