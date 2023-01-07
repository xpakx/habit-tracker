import { BattlePosition } from "./battle-position";

export interface MoveEvent {
    position: BattlePosition;
    shipId: number;
}