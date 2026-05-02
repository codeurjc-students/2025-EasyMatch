import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { LoginService } from './login.service';
import { UserService } from './user.service';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { AuthResponse } from '../models/auth/auth-response.model';
import { LoginRequest } from '../models/auth/login-request.model';

class HttpClientMock {
  post = jasmine.createSpy('post');
}

class UserServiceMock {
  getCurrentUser = jasmine.createSpy('getCurrentUser');
}

describe('LoginService', () => {
  let service: LoginService;
  let httpClientMock: HttpClientMock;
  let userServiceMock: UserServiceMock;

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    realname: 'Test User',
    email: 'test@test.com',
    birthDate: '2000-01-01',
    gender: true,
    description: 'Usuario test',
    roles: ['USER']
  };

  const mockAuthResponse: AuthResponse = {
    status: 'SUCCESS',
    message: 'Login correcto'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LoginService,
        {
          provide: HttpClient,
          useClass: HttpClientMock
        },
        {
          provide: UserService,
          useClass: UserServiceMock
        }
      ]
    });

    httpClientMock = TestBed.inject(HttpClient) as unknown as HttpClientMock;
    userServiceMock = TestBed.inject(UserService) as unknown as UserServiceMock;

    httpClientMock.post.and.returnValue(of({}));
    userServiceMock.getCurrentUser.and.returnValue(of(null));

    service = TestBed.inject(LoginService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should login and set current user correctly', () => {
    const loginRequest: LoginRequest = {
      username: 'test@test.com',
      password: '123456'
    };

    httpClientMock.post.and.returnValue(of(mockAuthResponse));
    userServiceMock.getCurrentUser.and.returnValue(of(mockUser));

    service.login(loginRequest).subscribe(user => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/auth/login`,
        loginRequest
      );

      expect(userServiceMock.getCurrentUser).toHaveBeenCalled();
      expect(user).toEqual(mockUser);
      expect(service.currentUser).toEqual(mockUser);
      expect(service.isLogged()).toBeTrue();
    });
  });

  it('should return null if getCurrentUser fails after login', () => {
    const loginRequest: LoginRequest = {
      username: 'test@test.com',
      password: '123456'
    };

    httpClientMock.post.and.returnValue(of(mockAuthResponse));
    userServiceMock.getCurrentUser.and.returnValue(
      throwError(() => new Error('Error user'))
    );

    service.login(loginRequest).subscribe(user => {
      expect(user).toBeNull();
      expect(service.currentUser).toBeNull();
    });
  });

  it('should clear current user on logout', () => {
    const logoutResponse: AuthResponse = {
      status: 'SUCCESS',
      message: 'Logout correcto'
    };

    service.setCurrentUser(mockUser);

    httpClientMock.post.and.returnValue(of(logoutResponse));

    service.logout().subscribe(response => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/auth/logout`,
        {},
        { withCredentials: true }
      );

      expect(response).toEqual(logoutResponse);
      expect(service.currentUser).toBeNull();
      expect(service.isLogged()).toBeFalse();
    });
  });

  it('should return true when user is logged', () => {
    service.setCurrentUser(mockUser);

    expect(service.isLogged()).toBeTrue();
  });

  it('should return false when user is not logged', () => {
    service.setCurrentUser(null);

    expect(service.isLogged()).toBeFalse();
  });

  it('should restore session and set current user', () => {
    httpClientMock.post.calls.reset();
    userServiceMock.getCurrentUser.calls.reset();

    httpClientMock.post.and.returnValue(of({}));
    userServiceMock.getCurrentUser.and.returnValue(of(mockUser));

    service.restoreSession();

    expect(httpClientMock.post).toHaveBeenCalledWith(
      '/api/v1/auth/refresh',
      {},
      { withCredentials: true }
    );

    expect(userServiceMock.getCurrentUser).toHaveBeenCalledWith({
      headers: {
        'X-Skip-Interceptor': 'true'
      }
    });
  });
});