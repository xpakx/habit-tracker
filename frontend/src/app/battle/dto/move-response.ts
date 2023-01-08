import { AttackResult } from "./attack-result";
import { MoveResult } from "./move-result";

export interface MoveResponse {
    action: String;
    success: boolean;
    move?: MoveResult;
    attack?: AttackResult;
}