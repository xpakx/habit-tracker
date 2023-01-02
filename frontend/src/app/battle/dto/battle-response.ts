import { BattleShip } from "./battle-ship";

export interface BattleResponse {
    battleId: number;
    width: number;
    height: number;
    finished: boolean;
    started: boolean;
    objective: String;
    turn: number;
    ships: BattleShip[];
    enemyShips: BattleShip[];
}