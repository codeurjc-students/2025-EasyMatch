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
  result: {
    team1Name: string;
    team2Name: string;
    team1Score: number;
    team2Score: number;
    team1Sets: number;
    team2Sets: number;
    team1GamesPerSet: number[];
    team2GamesPerSet: number[];
  }
  team1Players: User[];
  team2Players: User[];

}
