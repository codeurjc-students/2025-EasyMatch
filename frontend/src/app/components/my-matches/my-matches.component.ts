import { Component, computed, DestroyRef, inject, Inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchService } from '../../service/match.service';
import { UserService } from '../../service/user.service';
import { Match } from '../../models/match.model';
import { HeaderComponent } from '../header/header.component';
import { MatchComponent } from '../match/match';
import { MatIcon } from "@angular/material/icon";
import { MatDivider } from "@angular/material/divider";
import { LoginService } from '../../service/login.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-my-matches',
  standalone: true,
  imports: [CommonModule, HeaderComponent, MatchComponent, MatIcon, MatDivider],
  templateUrl: './my-matches.component.html',
  styleUrls: ['./my-matches.component.scss']
})
export class MyMatchesComponent implements OnInit {

  matches = signal<Match[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  private userService = inject(UserService);
  private loginService = inject(LoginService);
  private destroyRef = inject(DestroyRef);


  openMatches = computed(() =>
    this.matches().filter(m =>
      m.state === true
    )
  );

  closedMatches = computed(() =>
    this.matches().filter(m =>
      m.state === false
    )
  );

   ngOnInit(): void {
    this.loginService.currentUser$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(user => {
        if (!user?.id) {
          this.matches.set([]);
          this.loading.set(false);
          this.error.set('Usuario no identificado');
          return;
        }

        this.loading.set(true);
        this.error.set(null);
        
        this.userService.getUserMatches(user.id).subscribe({
          next: (data) => {
            this.matches.set(data);
            this.loading.set(false);
          },
          error: (err) => {
            console.error('Error cargando partidos:', err);
            this.error.set('No pudimos cargar tus partidos. Intenta de nuevo.');
            this.loading.set(false);
          }
        });
      });
  }
}
