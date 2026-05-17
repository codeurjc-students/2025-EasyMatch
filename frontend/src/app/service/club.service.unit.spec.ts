import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import { ClubService } from './club.service';
import { environment } from '../../environments/environment';
import { Club } from '../models/club.model';

class HttpClientMock {
  get = jasmine.createSpy('get');
  post = jasmine.createSpy('post');
  put = jasmine.createSpy('put');
  delete = jasmine.createSpy('delete');
}

describe('ClubService', () => {
  let service: ClubService;
  let httpClientMock: HttpClientMock;

  const mockClub: Club = {
      id: 1,
      name: 'Club Central',
      city: 'Madrid',
      sports: [],
      address: '',
      numberOfCourts: [],
      schedule: {
          openingTime: '09:00',
          closingTime: '21:00'
      },
      priceRange: {
          minPrice: 10,
          maxPrice: 50,
          unit: '€'
      }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClubService,
        {
          provide: HttpClient,
          useClass: HttpClientMock
        }
      ]
    });

    service = TestBed.inject(ClubService);
    httpClientMock = TestBed.inject(HttpClient) as unknown as HttpClientMock;
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should get one club correctly', () => {
    httpClientMock.get.and.returnValue(of(mockClub));

    service.getClub(1).subscribe(club => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/clubs/1`
      );

      expect(club).toEqual(mockClub);
    });
  });

  it('should get clubs with pagination correctly', () => {
    const paginatedResponse = {
      content: [mockClub],
      totalElements: 1,
      totalPages: 1,
      number: 0
    };

    httpClientMock.get.and.returnValue(of(paginatedResponse));

    service.getClubs().subscribe(response => {
      expect(httpClientMock.get).toHaveBeenCalled();

      expect(response.content.length).toBe(1);
      expect(response.totalElements).toBe(1);
      expect(response.content[0]).toEqual(mockClub);
    });
  });

  it('should create club correctly', () => {
    const payload: Partial<Club> = {
      name: 'Nuevo Club',
      city: 'Barcelona',
      sports: []
    };

    const createdClub: Club = {
        id: 2,
        name: 'Nuevo Club',
        city: 'Barcelona',
        sports: [],
        address: '',
        numberOfCourts: [],
        schedule: {
            openingTime: '09:00',
            closingTime: '21:00'
        },
        priceRange: {
            minPrice: 10,
            maxPrice: 50,
            unit: '€'
        }
    };

    httpClientMock.post.and.returnValue(of(createdClub));

    service.createClub(payload).subscribe(club => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/clubs/`,
        payload
      );

      expect(club).toEqual(createdClub);
    });
  });

  it('should update club correctly', () => {
    const payload: Partial<Club> = {
      name: 'Club Actualizado'
    };

    const updatedClub: Club = {
      ...mockClub,
      ...payload
    };

    httpClientMock.put.and.returnValue(of(updatedClub));

    service.updateClub(1, payload).subscribe(club => {
      expect(httpClientMock.put).toHaveBeenCalledWith(
        `${environment.apiUrl}/clubs/1`,
        payload
      );

      expect(club.name).toBe('Club Actualizado');
    });
  });

  it('should delete club correctly', () => {
    httpClientMock.delete.and.returnValue(of(mockClub));

    service.deleteClub(1).subscribe(club => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/clubs/1`
      );

      expect(club).toEqual(mockClub);
    });
  });

  it('should get club image correctly', () => {
    const mockBlob = new Blob(['image-content'], {
      type: 'image/png'
    });

    httpClientMock.get.and.returnValue(of(mockBlob));

    service.getClubImage(1).subscribe(image => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/clubs/1/image`,
        {
          responseType: 'blob'
        }
      );

      expect(image).toEqual(mockBlob);
    });
  });

  it('should replace club image using FormData', () => {
    const mockFile = new File(
      ['image-content'],
      'club-image.png',
      { type: 'image/png' }
    );

    httpClientMock.put.and.returnValue(of({}));

    service.replaceClubImage(1, mockFile).subscribe(() => {
      expect(httpClientMock.put).toHaveBeenCalled();

      const requestBody =
        httpClientMock.put.calls.mostRecent().args[1];

      expect(requestBody instanceof FormData).toBeTrue();
    });
  });
});