import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import { MessageService } from './message.service';
import { environment } from '../../environments/environment';
import { ChatMessage } from '../models/chat-message.model';

class HttpClientMock {
  get = jasmine.createSpy('get');
  put = jasmine.createSpy('put');
  delete = jasmine.createSpy('delete');
}

describe('MessageService', () => {
  let service: MessageService;
  let httpClientMock: HttpClientMock;

  const mockMessage: ChatMessage = {
      matchId: 1,
      content: 'Mensaje de prueba',
      senderUsername: 'usuario1',
      type: 'CHAT_MESSAGE',
      timestamp: '2026-05-10T18:00:00.000Z'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MessageService,
        {
          provide: HttpClient,
          useClass: HttpClientMock
        }
      ]
    });

    service = TestBed.inject(MessageService);
    httpClientMock = TestBed.inject(HttpClient) as unknown as HttpClientMock;
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should get one message correctly', () => {
    httpClientMock.get.and.returnValue(of(mockMessage));

    service.getMessage(1).subscribe(message => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/messages/1`
      );

      expect(message).toEqual(mockMessage);
    });
  });

  it('should get messages with pagination correctly', () => {
    const paginatedResponse = {
      content: [mockMessage],
      totalElements: 1,
      totalPages: 1,
      number: 0
    };

    httpClientMock.get.and.returnValue(of(paginatedResponse));

    service.getMessages().subscribe(response => {
      expect(httpClientMock.get).toHaveBeenCalled();

      expect(response.content.length).toBe(1);
      expect(response.totalElements).toBe(1);
      expect(response.content[0]).toEqual(mockMessage);
    });
  });

  it('should update message correctly', () => {
    const payload: Partial<ChatMessage> = {
      content: 'Mensaje actualizado'
    };

    const updatedMessage: ChatMessage = {
      ...mockMessage,
      ...payload
    };

    httpClientMock.put.and.returnValue(of(updatedMessage));

    service.updatechatMessage(1, payload).subscribe(message => {
      expect(httpClientMock.put).toHaveBeenCalledWith(
        `${environment.apiUrl}/messages/1`,
        payload
      );

      expect(message.content).toBe('Mensaje actualizado');
    });
  });

  it('should delete message correctly', () => {
    httpClientMock.delete.and.returnValue(of(mockMessage));

    service.deleteMessage(1).subscribe(message => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/messages/1`
      );

      expect(message).toEqual(mockMessage);
    });
  });
});