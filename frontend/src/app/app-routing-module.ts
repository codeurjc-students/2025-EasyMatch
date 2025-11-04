import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canActivateAuth } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'matches', loadComponent: () => import('./components/match-list/match-list').then(m => m.MatchListComponent), canActivate: [canActivateAuth] },
  { path: 'clubs', loadComponent: () => import('./components/club-list/club-list.component').then(m => m.ClubListComponent), canActivate: [canActivateAuth]},
  { path: '**', redirectTo: 'login' },
  {path: 'error',
  loadComponent: () =>
    import('./components/error/error.component').then(m => m.ErrorComponent)},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
