import { BattlePosition } from "./battle-position";

export interface BattleShip {
    id: number;
    shipId: number;
    name: String;
    code: String;
    rarity: number;
    size: number;
    hp: number;
    strength: number;
    hitRate: number;
    criticalRate: number;
    destroyed: boolean;
    damaged: boolean;
    prepared: boolean;
    action: boolean;
    movement: boolean;
    enemy: boolean;
    position: BattlePosition;
}