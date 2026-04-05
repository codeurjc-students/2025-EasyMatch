
import { Injectable, signal, computed } from '@angular/core';
import { UserSportProfile } from './models/user-sport-profile.model';


@Injectable({ providedIn: 'root' })
export class GlobalSportState {

  private _sportProfile = signal<UserSportProfile | null>(null);

  sportProfile = this._sportProfile.asReadonly();

  level = computed(() => this._sportProfile()?.level ?? null);

  setSportProfile(profile: UserSportProfile | null): void {
    this._sportProfile.set(profile);
  }

  clear(): void {
    this._sportProfile.set(null);
  }
}