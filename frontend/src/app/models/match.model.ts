import { User } from "./user.model";

export interface Match {
  id?: number;
  date: Date;
  type: boolean;
  isPrivate: boolean;
  state: boolean;
  organizer: User;
  sport: string;
}
