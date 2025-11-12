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
import { By } from '@angular/platform-browser';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { RouterTestingModule } from '@angular/router/testing';


const mockUser = {
  id: 1,
  realname: 'Carlos García',
  username: 'cgarcia',
  email: 'carlos@example.com',
  birthDate: new Date('1990-05-14'),
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
        { provide: MatDialog, useClass: MockMatDialog }
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
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('h2')?.textContent).toContain('Carlos García');
    expect(compiled.querySelector('.username')?.textContent).toContain('@cgarcia');
    expect(compiled.querySelector('#totalMatches h3')?.textContent).toContain('120');
    expect(compiled.querySelector('#winRate h3')?.textContent).toContain('66.67');
  });

  /* it('should call onEditProfile() when edit button clicked', () => {
    spyOn(component, 'onEditProfile');
    const btn = fixture.debugElement.query(By.css('button[color="primary"]'));
    btn.triggerEventHandler('click');
    expect(component.onEditProfile).toHaveBeenCalled();
  }); */


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
