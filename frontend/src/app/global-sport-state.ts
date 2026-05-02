
import { Injectable, signal, computed } from '@angular/core';
import { UserSportProfile } from './models/user-sport-profile.model';


@Injectable({ providedIn: 'root' })
export class GlobalSportState {

  private _sportProfile = signal<UserSportProfile | null>(null);
 private _sportId = signal<number | null>(null);

  sportProfile = this._sportProfile.asReadonly();
  sportId = this._sportId.asReadonly();

  level = computed(() => this._sportProfile()?.level ?? null);

  setSportProfile(profile: UserSportProfile | null): void {
    this._sportProfile.set(profile);
  }

  setSportId(id: number | null) {
    this._sportId.set(id);
  }

  clear(): void {
    this._sportProfile.set(null);
  }
}