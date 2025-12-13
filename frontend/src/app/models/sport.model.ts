import { ScoringType } from "./scoring-type";

export interface Sport{
    id?: number;
    name: string;
    modes: {name: string, playersPerGame: number}[];
    scoringType: ScoringType;
}