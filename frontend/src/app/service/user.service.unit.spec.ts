import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { UserService } from './user.service';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';

class HttpClientMock {
  get = jasmine.createSpy('get');
  post = jasmine.createSpy('post');
  put = jasmine.createSpy('put');
  delete = jasmine.createSpy('delete');
}

describe('UserService', () => {
  let service: UserService;
  let httpClientMock: HttpClientMock;

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

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        {
          provide: HttpClient,
          useClass: HttpClientMock
        }
      ]
    });

    service = TestBed.inject(UserService);
    httpClientMock = TestBed.inject(HttpClient) as unknown as HttpClientMock;
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should get current user correctly', () => {
    httpClientMock.get.and.returnValue(of(mockUser));

    service.getCurrentUser().subscribe(user => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/users/me`,
        {}
      );

      expect(user).toEqual(mockUser);
    });
  });

  it('should return null when getCurrentUser receives 500 error', () => {
    httpClientMock.get.and.returnValue(
      throwError(() => ({ status: 500 }))
    );

    service.getCurrentUser().subscribe(user => {
      expect(user).toBeNull();
    });
  });

  it('should throw error when getCurrentUser receives non-500 error', () => {
    httpClientMock.get.and.returnValue(
      throwError(() => ({ status: 401 }))
    );

    service.getCurrentUser().subscribe({
      next: () => fail('Should throw error'),
      error: error => {
        expect(error.status).toBe(401);
      }
    });
  });

  it('should register user and transform birthDate to Date', () => {
    const payload: Partial<User> = {
      username: 'testuser',
      realname: 'Test User',
      email: 'test@test.com',
      birthDate: new Date('2000-01-01'),
      gender: true
    };

    const backendResponse = {
      ...mockUser,
      birthDate: '2000-01-01T00:00:00.000Z'
    };

    httpClientMock.post.and.returnValue(of(backendResponse));

    service.registerUser(payload).subscribe(user => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/users/`,
        jasmine.objectContaining({
          birthDate: jasmine.any(String)
        })
      );

      expect(user.birthDate instanceof Date).toBeTrue();
    });
  });

  it('should delete user correctly', () => {
    httpClientMock.delete.and.returnValue(of(mockUser));

    service.deleteUser(1).subscribe(user => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/users/1`
      );

      expect(user).toEqual(mockUser);
    });
  });

  it('should update user correctly', () => {
    const payload: Partial<User> = {
      username: 'updatedUser'
    };

    httpClientMock.put.and.returnValue(of({
      ...mockUser,
      ...payload
    }));

    service.updateUser(1, payload).subscribe(user => {
      expect(httpClientMock.put).toHaveBeenCalledWith(
        `${environment.apiUrl}/users/1`,
        payload
      );

      expect(user.username).toBe('updatedUser');
    });
  });

  it('should get user by id correctly', () => {
    httpClientMock.get.and.returnValue(of(mockUser));

    service.getUserById(1).subscribe(user => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/users/1`
      );

      expect(user).toEqual(mockUser);
    });
  });

  it('should get all users with pagination correctly', () => {
    const paginatedResponse = {
      content: [mockUser],
      totalElements: 1,
      totalPages: 1,
      number: 0
    };

    httpClientMock.get.and.returnValue(of(paginatedResponse));

    service.getAllUsers().subscribe(response => {
      expect(httpClientMock.get).toHaveBeenCalled();

      expect(response.content.length).toBe(1);
      expect(response.totalElements).toBe(1);
      expect(response.content[0]).toEqual(mockUser);
    });
  });

  it('should set image using FormData in replaceUserImage', () => {
    const mockFile = new File(
      ['image-content'],
      'profile.png',
      { type: 'image/png' }
    );

    httpClientMock.put.and.returnValue(of({}));

    service.replaceUserImage(1, mockFile).subscribe(() => {
      expect(httpClientMock.put).toHaveBeenCalled();

      const requestBody = httpClientMock.put.calls.mostRecent().args[1];

      expect(requestBody instanceof FormData).toBeTrue();
    });
  });
});