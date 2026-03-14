import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { filter, map, switchMap, take } from "rxjs/operators";
import { LoginService } from "../service/login.service";

export const adminGuard: CanActivateFn = () => {

  const loginService = inject(LoginService);
  const router = inject(Router);

  return loginService.sessionReady$.pipe(

    // Esperar a que restoreSession termine
    filter(ready => ready),
    take(1),

    switchMap(() => loginService.isAdmin$),

    map(isAdmin => {

      if (loginService.isLogged() && isAdmin) {
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