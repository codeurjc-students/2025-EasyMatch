import { Club } from "./club.model";
import { Sport } from "./sport.model";
import { User } from "./user.model";

export interface Match {
  
  id?: number;
  date: Date;
  type: boolean;
  isPrivate: boolean;
  state: boolean;
  organizer: User;
  sport: Sport;
  price: number;
  club: Club;
  players: User[];

}
