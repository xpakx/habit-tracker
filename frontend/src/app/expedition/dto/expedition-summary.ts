export interface ExpeditionSummary {
    id: number;
    userId: number;
    start: Date;
    end: Date;
    returnEnd: Date;
    finished: boolean;
    returning: boolean;
    result: String;
}