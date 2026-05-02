import { TestBed } from "@angular/core/testing";
import { HttpClient } from "@angular/common/http";
import { of } from "rxjs";

import { MatchService } from "./match.service";
import { environment } from "../../environments/environment";
import { Match } from "../models/match.model";
import { JoinMatchResponse } from "../models/join-match-response";
import { MatchResult } from "../models/match-result.model";

class HttpClientMock {
  get = jasmine.createSpy("get");
  post = jasmine.createSpy("post");
  put = jasmine.createSpy("put");
  delete = jasmine.createSpy("delete");
}

describe("MatchService", () => {
  let service: MatchService;
  let httpClientMock: HttpClientMock;

  const mockMatch: Match = {
    id: 1,
    date: new Date("2026-01-10T18:00:00.000Z"),
    type: true,
    isPrivate: false,
    state: true,
    modeSelected: 0,
    price: 10,

    organizer: {
      id: 1,
      username: "organizer",
      realname: "Organizer User",
      email: "organizer@test.com",
      birthDate: "1995-01-01",
      gender: true,
      description: "Organizador",
      roles: ["USER"],
    } as any,

    sport: {
      id: 1,
      name: "Pádel",
      modes: [],
    } as any,

    club: {
      id: 1,
      name: "Club Central",
      city: "Madrid",
      sports: [],
    } as any,

    result: {
      team1Name: "Equipo A",
      team2Name: "Equipo B",
      team1Score: 6,
      team2Score: 4,
      team1Sets: 1,
      team2Sets: 0,
      team1GamesPerSet: [6],
      team2GamesPerSet: [4],
    },

    team1Players: [],
    team2Players: [],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MatchService,
        {
          provide: HttpClient,
          useClass: HttpClientMock,
        },
      ],
    });

    service = TestBed.inject(MatchService);
    httpClientMock = TestBed.inject(HttpClient) as unknown as HttpClientMock;
  });

  it("should create", () => {
    expect(service).toBeTruthy();
  });

  it("should get match by id correctly", () => {
    httpClientMock.get.and.returnValue(of(mockMatch));

    service.getMatch(1).subscribe((match) => {
      expect(httpClientMock.get).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1`,
      );

      expect(match).toEqual(mockMatch);
    });
  });

  it("should create match and transform date to Date", () => {
    const payload: Partial<Match> = {
      date: new Date("2026-01-10"),
      price: 10,
      type: true,
    };

    const backendResponse = {
      ...mockMatch,
      date: "2026-01-10T18:00:00.000Z",
    };

    httpClientMock.post.and.returnValue(of(backendResponse));

    service.createMatch(payload).subscribe((match) => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches`,
        jasmine.objectContaining({
          date: jasmine.any(String),
        }),
      );

      expect(match.date instanceof Date).toBeTrue();
    });
  });

  it("should join match correctly", () => {
    const response: JoinMatchResponse = {
      status: "SUCCESS",
      message: "Usuario unido correctamente",
    };

    httpClientMock.put.and.returnValue(of(response));

    service.joinMatch(1, "A").subscribe((result) => {
      expect(httpClientMock.put).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1/users/me`,
        { team: "A" },
      );

      expect(result).toEqual(response);
    });
  });

  it("should leave match correctly", () => {
    httpClientMock.delete.and.returnValue(of({}));

    service.leaveMatch(1).subscribe(() => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1/users/me`,
      );
    });
  });

  it("should delete match correctly", () => {
    httpClientMock.delete.and.returnValue(of(mockMatch));

    service.deleteMatch(1).subscribe((match) => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1`,
      );

      expect(match).toEqual(mockMatch);
    });
  });

  it("should update match correctly", () => {
    const payload: Partial<Match> = {
      price: 20,
    };

    httpClientMock.put.and.returnValue(
      of({
        ...mockMatch,
        ...payload,
      }),
    );

    service.updateMatch(1, payload).subscribe((match) => {
      expect(httpClientMock.put).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1`,
        payload,
      );

      expect(match.price).toBe(20);
    });
  });

  it("should add match result correctly", () => {
    const result: MatchResult = {
      team1Score: 6,
      team2Score: 4,
    } as MatchResult;

    httpClientMock.post.and.returnValue(of(result));

    service.addMatchResult(1, result).subscribe((response) => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1/result`,
        result,
      );

      expect(response).toEqual(result);
    });
  });

  it("should get all matches correctly", () => {
    const paginatedResponse = {
      content: [mockMatch],
      totalElements: 1,
      totalPages: 1,
      number: 0,
    };

    httpClientMock.get.and.returnValue(of(paginatedResponse));

    service.getAllMatches().subscribe((response) => {
      expect(httpClientMock.get).toHaveBeenCalled();

      expect(response.content.length).toBe(1);
      expect(response.totalElements).toBe(1);
      expect(response.content[0]).toEqual(mockMatch);
    });
  });

  it("should add player to team 1 correctly", () => {
    httpClientMock.post.and.returnValue(of({}));

    service.addPlayerToTeam1(1, 5).subscribe(() => {
      expect(httpClientMock.post).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1/team1Players/5`,
        { userId: 5 },
      );
    });
  });

  it("should remove player from team 2 correctly", () => {
    httpClientMock.delete.and.returnValue(of({}));

    service.removePlayerFromTeam2(1, 5).subscribe(() => {
      expect(httpClientMock.delete).toHaveBeenCalledWith(
        `${environment.apiUrl}/matches/1/team2Players/5`,
      );
    });
  });
});
