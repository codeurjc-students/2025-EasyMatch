import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { firstValueFrom } from "rxjs";
import { LoginService } from "../service/login.service";

export const adminGuard: CanActivateFn = () => {

  const loginService = inject(LoginService);
  const router = inject(Router);

  if (loginService.isAdmin$) {
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
};