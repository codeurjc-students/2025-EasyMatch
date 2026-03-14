import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { filter, map, switchMap, take } from "rxjs/operators";
import { LoginService } from "../service/login.service";

export const canActivateAuth: CanActivateFn = () => {

  const loginService = inject(LoginService);
  const router = inject(Router);

  return loginService.sessionReady$.pipe(
    filter(ready => ready),
    take(1),
    switchMap(() => loginService.currentUser$),
    map(user => {

      if (user) return true;

      router.navigate(['/error'], {
        queryParams: {
          code: 401,
          title: 'No autenticado',
          message: 'Debes iniciar sesión para acceder a esta sección.'
        }
      });

      return false;

    })
  );

};