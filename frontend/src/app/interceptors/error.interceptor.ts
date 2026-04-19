import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';

let isRefreshing = false;

const refreshSubject = new BehaviorSubject<boolean | null>(null);

export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  const router = inject(Router);
  const https = inject(HttpClient);

  const isRefreshCall = req.url.includes('/auth/refresh');
  const isLoginCall = req.url.includes('/auth/login');

  if (req.headers.has('X-Skip-Interceptor')) return next(req);

  return next(req).pipe(
    catchError((err) => {

      const backendMessage =
          err?.error?.message ||
          err?.error?.error ||
          err?.error ||
          'Se produjo un conflicto.'; 

      if (err.status === 401) {

        if (isRefreshCall || isLoginCall) {
          return throwError(() => err);
        }

        if (!isRefreshing) {
          isRefreshing = true;
          refreshSubject.next(null);
          console.warn('Token expirado, intentando refrescar...');
          return https.post('/api/v1/auth/refresh', {}, { withCredentials: true }).pipe(

            switchMap(() => {
              isRefreshing = false;
              refreshSubject.next(true);
              return next(req);
            }),

            catchError(() => {
              isRefreshing = false;
              refreshSubject.next(false);

              router.navigate(['/error'], {
                queryParams: {
                  code: 401,
                  title: 'No autenticado',
                  message: 'Debes iniciar sesión para acceder a esta sección.'
                }
              });

              return throwError(() => err);
            })
          );
        }


        return refreshSubject.pipe(
          filter(value => value !== null),
          take(1),
          switchMap((success) => {
            if (success) {
              return next(req);
            }

            return throwError(() => err);
          })
        );
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
