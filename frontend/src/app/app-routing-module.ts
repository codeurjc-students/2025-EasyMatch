import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canActivateAuth } from './guards/auth.guard';
import { ErrorPageComponent } from './components/error/error-page.component';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  // PUBLIC ROUTES
  { path: '', redirectTo: 'matches', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent : () => import('./components/register/register.component').then(c => c.RegisterComponent)},
  { path: 'matches', loadComponent: () => import('./components/match-list/match-list').then(m => m.MatchListComponent)},
  { path: 'clubs', loadComponent: () => import('./components/club-list/club-list.component').then(m => m.ClubListComponent)},

  { path: 'admin', loadChildren: () => import('./components/admin/admin-routing').then(m => m.ADMIN_ROUTES), canActivate: [adminGuard] },
  { path: 'matches/create', loadComponent : () => import('./components/match-create/match-create.component').then(c => c.MatchCreateComponent), canActivate: [canActivateAuth] },
  { path: 'profile', loadComponent : () => import('./components/user/user.component').then(c => c.UserComponent), canActivate: [canActivateAuth] },
  { path: 'my-matches', loadComponent: () => import('./components/my-matches/my-matches.component').then(m => m.MyMatchesComponent), canActivate: [canActivateAuth] },
  {
    path: '**',
    component: ErrorPageComponent,
    data: {
      code: 404,
      title: 'Página no encontrada',
      message: 'La página que buscas no existe o fue movida.'
    }
  },
  {path: 'error', loadComponent: () => import('./components/error/error-page.component').then(m => m.ErrorPageComponent)},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
