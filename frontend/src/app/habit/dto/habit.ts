import { HabitContextMin } from "./habit-context-min";
import { HabitTrigger } from "./habit-trigger";

export interface Habit {
    id: number;
    name: String;
    description: String;
    context: HabitContextMin;
    trigger: HabitTrigger;

    interval: number;
    dailyCompletions: number;
    start: Date;
    nextDue: Date;
}