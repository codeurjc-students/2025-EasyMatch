import { TestBed } from "@angular/core/testing";
import { HttpClientModule } from "@angular/common/http";
import { ClubService } from "./club.service";
import { LoginService } from "./login.service";
import { Club } from "../models/club.model";

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;

describe('ClubService', () => {
  let service: ClubService;
  let loginService: LoginService;

  const adminLoginRequest = {
    username: 'admin@emeal.com',
    password: 'admin'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [ClubService],
    });
    service = TestBed.inject(ClubService);
    loginService = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it("getClubs should return API's value as an Observable", (done: DoneFn) => {
    service.getClubs(0, 10).subscribe(response => {
      expect(response).toBeTruthy();
      expect(Array.isArray(response.content)).toBeTrue();
      expect(response.content.length).toBeGreaterThan(0);
      expect(response.totalElements).toBe(5);
      done();
    });
  });

  it("getClub should return a single club by id", (done: DoneFn) => {
    const testClubId = 1;
    service.getClub(testClubId).subscribe(response => {
      expect(response).toBeTruthy();
      expect(response.id).toBe(testClubId);
      expect(response.name).toBe("Tennis Club Elite");
      expect(response.city).toBe("Madrid");
      done();
    });
  });

  it('createClub should create a new club when admin is logged in', (done: DoneFn) => {
    const mockClub: Partial<Club> = {
      name: "Club Padel Madrid",
      city: "Madrid",
      address: "Plaza de Cristiano Ronaldo, 7",
      phone: "777777777",
      email: "clubpadelmadrid@emeal.com",
      web: "www.clubpadelmadrid.com",
      schedule: {
        openingTime: "7:45",
        closingTime: "21:15"
      },
      priceRange: {
        minPrice: Number(9.49),
        maxPrice: Number(14.49),
        unit: "€/hora"
      }
    };
    loginService.login(adminLoginRequest).subscribe({
      next: () => {
        service.createClub(mockClub).subscribe({
          next: (createdClub : Club) => {
            expect(createdClub).toBeTruthy();
            expect(createdClub.name).toBe("Club Padel Madrid");
            expect(createdClub.sports).toEqual([]);
            done();
          },
          error: err => {
            fail(`createClub failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed : ${err.message}`);
        done();
      }
    })
  });

  it('deleteClub should delete club when admin is logged in', (done) => {
      const testClubId = 2;
  
      loginService.login(adminLoginRequest).subscribe({
        next: () => {
          service.deleteClub(testClubId).subscribe({
            next: (club: Club) => {
              expect(club.id).toBe(2);
              done();
            },
            error: err => {
              fail(`deleteClub failed : ${err.message}`);
              done();
            }
          });
        },
        error: err => {
          fail(`login failed : ${err.message}`);
          done();
        }
      });
    });

    it('replaceClub should update an existing club', (done: DoneFn) => {


      const mockClub: Partial<Club> = {
        name: "Club Padel Madrid",
        city: "Madrid",
        address: "Plaza de Cristiano Ronaldo, 7",
        phone: "777777777",
        email: "clubpadelmadrid@emeal.com",
        web: "www.clubpadelmadrid.com",
        schedule: {
          openingTime: "7:45",
          closingTime: "21:15"
        },
        priceRange: {
          minPrice: Number(9.49),
          maxPrice: Number(14.49),
          unit: "€/hora"
        }
      };

      loginService.login(adminLoginRequest).subscribe({
        next: () => {

          service.createClub(mockClub).subscribe({
            next: created => {
              expect(created).toBeTruthy();

              const updatedClub = {
                ...created,
                name: "Club Updated Selenium",
                phone: "965432187",
              };

              service.updateClub(created.id, updatedClub).subscribe({
                next: updated => {
                  expect(updated).toBeTruthy();
                  expect(updated.name).toBe("Club Updated Selenium");
                  expect(updated.phone).toBe("965432187")
                  done();
                },
                error: err => {
                  fail(`replaceClub failed: ${err.message}`);
                  done();
                }
              });

            }
          });

        }
      });
    });

});
