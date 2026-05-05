import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { MessageService } from './message.service';
import { environment } from '../../environments/environment';
import { ChatMessage } from '../models/chat-message.model';
import { provideRouter } from '@angular/router';

describe('MessageService Integration Test', () => {
  let service: MessageService;
  let httpMock: HttpTestingController;

  const mockMessage: ChatMessage = {
      matchId: 1,
      content: 'Mensaje de prueba',
      senderUsername: 'usuario1',
      type: 'CHAT_MESSAGE',
      timestamp: '2026-05-10T18:00:00.000Z'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MessageService, provideRouter([])]
    });

    service = TestBed.inject(MessageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {

    httpMock.verify();
  });

  it('should get one message correctly', () => {
    service.getMessage(1).subscribe(message => {
      expect(message).toEqual(mockMessage);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/messages/1`
    );

    expect(req.request.method).toBe('GET');

    req.flush(mockMessage);
  });

  it('should get paginated messages correctly', () => {
    const paginatedResponse = {
      content: [mockMessage],
      totalElements: 1,
      totalPages: 1,
      number: 0
    };

    service.getMessages().subscribe(response => {
      expect(response.content.length).toBe(1);
      expect(response.totalElements).toBe(1);
      expect(response.content[0]).toEqual(mockMessage);
    });

    const req = httpMock.expectOne(req =>
      req.url === `${environment.apiUrl}/messages` &&
      req.params.get('page') === '0' &&
      req.params.get('size') === '10' &&
      req.params.get('sort') === 'id,asc'
    );

    expect(req.request.method).toBe('GET');

    req.flush(paginatedResponse);
  });

  it('should update message correctly', () => {
    const payload: Partial<ChatMessage> = {
      content: 'Mensaje actualizado integración'
    };

    const updatedMessage: ChatMessage = {
      ...mockMessage,
      ...payload
    };

    service.updatechatMessage(1, payload).subscribe(message => {
      expect(message.content).toBe('Mensaje actualizado integración');
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/messages/1`
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(payload);

    req.flush(updatedMessage);
  });

  it('should delete message correctly', () => {
    service.deleteMessage(1).subscribe(message => {
      expect(message).toEqual(mockMessage);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/messages/1`
    );

    expect(req.request.method).toBe('DELETE');

    req.flush(mockMessage);
  });
});