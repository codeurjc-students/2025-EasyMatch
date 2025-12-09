import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  const router = inject(Router);

  return next(req).pipe(
    catchError((err) => {

	  const backendMessage =
        err?.error?.message ||
        err?.error?.error ||
        err?.error ||
        'Se produjo un conflicto.'; 
      if (err.status === 401) {
        router.navigate(['/error'], {
          queryParams: {
            code: 401,
            title: 'No autenticado',
            message: 'Debes iniciar sesión para acceder a esta sección.'
          }
        });
      }

      else if (err.status === 403) {
        router.navigate(['/error'], {
          queryParams: {
            code: 403,
            title: 'Acceso denegado',
            message: 'No tienes permisos para acceder a este recurso.'
          }
        });
      }

      else if (err.status === 404) {
        router.navigate(['/error'], {
          queryParams: {
            code: 404,
            title: 'No encontrado',
            message: 'El recurso solicitado no existe.'
          }
        });
      }
	  else if (err.status === 409) {
        router.navigate(['/error'], {
          queryParams: {
            code: 409,
            title: 'Conflicto',
            message: backendMessage
          }
        });
      }


      else if (err.status >= 500) {
        router.navigate(['/error'], {
          queryParams: {
            code: err.status,
            title: 'Error interno',
            message: 'Se ha producido un error en el servidor.'
          }
        });
      }

      return throwError(() => err);
    })
  );
};
