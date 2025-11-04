
export interface Sport{
    id?: number;
    name: string;
    modes: {name: string, playersPerGame: number}[];
}