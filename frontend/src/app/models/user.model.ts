export interface User{
    id: number;
    realname: string;
    username: string;
    email: string;
    birthDate: Date;
    gender?: boolean;
    description: string;
    level: number,
    stats: {
        totalMatches: number,
        wins: number,
        draws: number,
        losses: number,
        winRate: number
    }
}