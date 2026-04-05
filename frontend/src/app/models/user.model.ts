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
    roles: string[];
}