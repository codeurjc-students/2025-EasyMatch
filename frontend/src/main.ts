import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app/app-routing-module';
import { ErrorInterceptor } from './app/interceptors/error.interceptor';
import { provideAnimations } from '@angular/platform-browser/animations';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import { LOCALE_ID } from '@angular/core';
import { provideNativeDateAdapter } from '@angular/material/core';

registerLocaleData(localeEs);

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([ErrorInterceptor])),
    provideAnimations(),
    provideNativeDateAdapter(),
    { provide: LOCALE_ID, useValue: 'es-ES' },
  ],
}).catch(err => console.error(err));
