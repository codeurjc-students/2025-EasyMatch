import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { firstValueFrom } from "rxjs";
import { LoginService } from "../service/login.service";


export const canActivateAuth: CanActivateFn = async () => {

  const loginService = inject(LoginService);
  const router = inject(Router);

  const isLogged = await firstValueFrom(loginService.userLoginOn);

  if (isLogged) {
    return true;
  }

  router.navigate(['/error'], {
    queryParams: {
      code: 401,
      title: 'No autenticado',
      message: 'Debes iniciar sesión para acceder a esta sección.'
    }
  });

  return false;
};