import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../service/user.service';
import { map } from 'rxjs/operators';

export const adminGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  return userService.getCurrentUser().pipe(
    map(user => {
      if (user && user.roles?.includes('ADMIN')) {
        return true;
      }

      router.navigate(['/error'], {
        queryParams: {
          code: 403,
          title: 'Acceso denegado',
          message: 'No tienes permisos para acceder a esta sección.'
        }
      });

      return false;
    })
  );
};