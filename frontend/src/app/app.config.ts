import { ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app-routing-module';
import { errorInterceptor } from './interceptors/error.interceptor';
import { provideNativeDateAdapter } from '@angular/material/core';
import { provideAnimations } from '@angular/platform-browser/animations';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
	  provideHttpClient(withInterceptors([errorInterceptor])),
     provideNativeDateAdapter(),
    { provide: LOCALE_ID, useValue: 'es-ES' },
    provideAnimations(),
  ]
};