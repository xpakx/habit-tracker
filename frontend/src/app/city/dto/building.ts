import { BuildingSummary } from "./building-summary";
import { City } from "./city";

export interface Building {
    id: number;
    city: City;
    building: BuildingSummary;
}
