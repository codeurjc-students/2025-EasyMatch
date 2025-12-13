import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-layout/admin-layout.component';
import { AdminUsersComponent } from './users/admin-users.component';
import { AdminUserCreateComponent } from './users/admin-user-create.component';
import { AdminClubsComponent } from './clubs/admin-clubs.component';
import { AdminClubCreateComponent } from './clubs/admin-club-create.component';
import { AdminMatchesComponent } from './matches/admin-matches.component';
import { AdminMatchCreateComponent } from './matches/admin-match-create.component';
import { AdminSportsComponent } from './sports/admin-sports.component';
import { AdminSportCreateComponent } from './sports/admin-sport-create.component';
import { adminGuard } from '../../guards/admin.guard';


export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    canActivate: [adminGuard],
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'users', pathMatch: 'full' },
      // USERS
      { path: 'users', component: AdminUsersComponent },
      { path: 'users/create', component: AdminUserCreateComponent },
      // CLUBS
      { path: 'clubs', component: AdminClubsComponent },
      { path: 'clubs/create', component: AdminClubCreateComponent },
      // MATCHES
      { path: 'matches', component: AdminMatchesComponent },
      { path: 'matches/create', component: AdminMatchCreateComponent },
      // SPORTS
      { path: 'sports', component: AdminSportsComponent },
      { path: 'sports/create', component: AdminSportCreateComponent },
    ],
  },
];
