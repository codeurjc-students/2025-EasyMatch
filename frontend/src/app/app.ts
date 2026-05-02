import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginService } from './service/login.service';
import { UserService } from './service/user.service';
import { GlobalSportState } from './global-sport-state';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: true,
  imports: [RouterOutlet],
})
export class AppComponent  {
  private loginService = inject(LoginService);
  private userService = inject(UserService);
  private sportFacade = inject(GlobalSportState);
  ngOnInit() {
    this.loginService.currentUser$.subscribe(user => {
      if (!user) return;

      // cargar primer deporte automáticamente
      this.userService.getUserSports(user.id).subscribe(sports => {
        if (!sports.length) return;

        const firstSportId = sports[0].id;

        this.userService.getUserSportProfile(user.id, firstSportId!).subscribe(profile => {
          this.sportFacade.setSportProfile(profile);
        });
      });
    });
  }
}