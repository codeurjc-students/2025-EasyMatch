import { TestBed } from '@angular/core/testing';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app';
import { MatchListComponent } from './components/match-list/match-list';
import { ComponentFixture } from '@angular/core/testing';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { LoginComponent } from './components/login/login.component';

describe('App', () => {
  let fixture: ComponentFixture<AppComponent>;
  let router: Router;
  let location: Location;

  const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'matches', component: MatchListComponent }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes(routes),
        AppComponent,
        LoginComponent,
        MatchListComponent
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    fixture = TestBed.createComponent(AppComponent);
    router.initialNavigation();
  });

  it('should create the app', () => {
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should navigate to /login by default', async () => {
    await fixture.whenStable();
    expect(location.path()).toBe('/login');
  });

  it('should render login title on /login', async () => {
    await fixture.whenStable();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent)
      .toContain('Bienvenido de vuelta');
  });
});