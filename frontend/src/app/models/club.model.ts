import { Sport } from "./sport.model";

export interface Club{
    id: number;
    name: string;
    city: string;
    address: string;
    phone?: string;
    sports: Sport[];
    email?: string;
    web?: string;
    schedule: {
        openingTime: string;
        closingTime: string;
    };
    priceRange: {
        minPrice: number;
        maxPrice: number;
        unit: string;
    };
    

}