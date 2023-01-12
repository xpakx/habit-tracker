import { ExpeditionShip } from "./expedition-ship";

export interface ExpeditionRequest {
    islandId?: number;
    ships: ExpeditionShip[];
}