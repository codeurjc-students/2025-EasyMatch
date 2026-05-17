import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import { SportService } from './sport.service';
import { environment } from '../../environments/environment';
import { Sport } from '../models/sport.model';
import { ScoringType } from '../models/scoring-type';

class HttpClientMock {
  get = jasmine.createSpy('get');
  post = jasmine.createSpy('post');
  put = jasmine.createSpy('put');
  delete = jasmine.createSpy('delete');
}

describe('SportService', () => {
  let service: SportService;
  let httpClientMock: HttpClientMock;

  const mockSport: Sport = {
      id: 1,
      name: 'Pádel',
      modes: [
          {
              name: 'Dobles',
              playersPerGame: 4
          }
      ],
      scoringType: ScoringType.SETS
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SportService,
        {
          provide: HttpClient,
          useClass: HttpClientMock
        }
      ]
    });

    service = TestBed.inject(SportService);
    httpClientMock = TestBed.inject(HttpClient) as unknown as HttpClientMock;
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should get one sport correctly', () => {
    httpClientMock.get.and.returnValue(of(mockSport));

    service.getSport(1).subscribe(sport => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/sports/1`,
      );

      expect(sport).toEqual(mockSport);
    });
  });

  it('should get all sports correctly', () => {
    const sportsResponse: Sport[] = [mockSport];

    httpClientMock.get.and.returnValue(of(sportsResponse));

    service.getSports().subscribe(sports => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/sports/`
      );

      expect(sports.length).toBe(1);
      expect(sports[0]).toEqual(mockSport);
    });
  });

  it('should create sport correctly', () => {
    const payload: Sport = {
        name: 'Tenis',
        modes: [
            {
                name: 'Individual',
                playersPerGame: 2
            }
        ],
        scoringType: ScoringType.SETS
    };

    const createdSport: Sport = {
      id: 2,
      ...payload
    };

    httpClientMock.post.and.returnValue(of(createdSport));

    service.createSport(payload).subscribe(sport => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/sports/`,
        payload
      );

      expect(sport).toEqual(createdSport);
    });
  });

  it('should update sport correctly', () => {
    const updatedSport: Sport = {
        id: 1,
        name: 'Pádel',
        modes: [
            {
                name: 'Dobles',
                playersPerGame: 4
            }
        ],
        scoringType: ScoringType.SETS
    };

    httpClientMock.put.and.returnValue(of(updatedSport));

    service.updateSport(1, updatedSport).subscribe(sport => {
      expect(httpClientMock.put).toHaveBeenCalledWith(
        `${environment.apiUrl}/sports/1`,
        updatedSport
      );

      expect(sport.name).toBe('Pádel');
    });
  });

  it('should delete sport correctly', () => {
    httpClientMock.delete.and.returnValue(of(mockSport));

    service.delete(1).subscribe(sport => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/sports/1`
      );

      expect(sport).toEqual(mockSport);
    });
  });
});