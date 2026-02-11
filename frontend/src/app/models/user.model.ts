import { LevelHistory } from "./level-history.model";

export interface User{
    id: number;
    realname: string;
    username: string;
    password?: string;
    email: string;
    birthDate: Date | string;
    gender?: boolean;
    description: string;
    level: number;
    stats: {
        totalMatches: number,
        wins: number,
        draws: number,
        losses: number,
        winRate: number
    }
    levelHistory : LevelHistory[];
    roles: string[];
}