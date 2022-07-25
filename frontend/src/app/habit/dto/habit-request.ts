export interface HabitRequest {
    name: String;
    description: String;
    interval: number;
    dailyCompletions: number;
    start: Date;
    contextId: number | undefined;
    triggerName: String;
}
