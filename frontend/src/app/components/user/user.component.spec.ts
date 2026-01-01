import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserComponent } from './user.component';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { of, Subject } from 'rxjs';
import { UserService } from '../../service/user.service';
import { HeaderComponent } from '../header/header.component';
import { RouterTestingModule } from '@angular/router/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';


const mockUser = {
  id: 1,
  realname: 'Carlos García',
  username: 'cgarcia',
  email: 'carlos@example.com',
  birthDate: '1990-05-14T00:00:00.000Z',
  gender: true,
  description: 'Jugador apasionado del fútbol',
  level: 7.5,
  stats: {
    totalMatches: 120,
    wins: 80,
    winRate: 66.67
  }
};

class MockUserService {
  getCurrentUser() {
    return of(mockUser);
  }
  deleteUser(id: number) {
    return of({ success: true });
  }
}

class MockMatDialog {
  open() {
    return {
      afterClosed: () => of(true)
    };
  }
}

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;
  let userService: UserService;
  let dialog: MatDialog;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatDividerModule,
        MatDialogModule,
        HeaderComponent,
        UserComponent,
        RouterTestingModule
      ],
      providers: [
        { provide: UserService, useClass: MockUserService },
        { provide: MatDialog, useClass: MockMatDialog },
        provideRouter([]),
        provideHttpClient()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    dialog = TestBed.inject(MatDialog);

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load and render user data', () => {
    component.user.set(mockUser as any);
    component.loading.set(false);

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    const name = compiled.querySelector('.user-info h2');
    const username = compiled.querySelector('.username');
    const totalMatches = compiled.querySelector('#totalMatches h3');
    const winRate = compiled.querySelector('#winRate h3');

    expect(name?.textContent?.trim()).toBe('Carlos García');
    expect(username?.textContent?.trim()).toBe('@cgarcia');
    expect(totalMatches?.textContent?.trim()).toBe('120');
    expect(winRate?.textContent?.trim()).toContain('66.67');
  });

  it('should not delete user if dialog canceled', () => {
    const dialogSpy = spyOn(dialog, 'open').and.returnValue({
      afterClosed: () => of(false)
    } as any);

    const deleteSpy = spyOn(userService, 'deleteUser').and.callThrough();

    component.onDeleteAccount(mockUser.id);

    expect(dialogSpy).toHaveBeenCalled();
    expect(deleteSpy).not.toHaveBeenCalled();
  });

  it('should show loading card when loading is true', () => {
    component.loading.set(true);
    fixture.detectChanges();
    const loadingCard = fixture.nativeElement.querySelector('.loading-card');
    expect(loadingCard).toBeTruthy();
    expect(loadingCard.textContent).toContain('Cargando perfil');
  });
});
