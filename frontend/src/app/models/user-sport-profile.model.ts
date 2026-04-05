export interface UserSportProfile {
    sportName: string;
    level: number;
    stats: {
        totalMatches: number,
        wins: number,
        draws: number,
        losses: number,
        winRate: number
    }
}