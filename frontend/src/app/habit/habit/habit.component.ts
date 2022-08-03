import { Component, Input, OnInit } from '@angular/core';
import { Habit } from '../dto/habit';
import { HabitCompletion } from '../dto/habit-completion';
import { HabitService } from '../habit.service';

@Component({
  selector: 'app-habit',
  templateUrl: './habit.component.html',
  styleUrls: ['./habit.component.css']
})
export class HabitComponent implements OnInit {
  @Input("habit") habit?: Habit;

  constructor(private habitService: HabitService) { }

  ngOnInit(): void {
  }

  completeHabit(): void {
    if(this.habit) {
      this.habitService.completeHabit(this.habit.id, {date: new Date()}).subscribe({
        next: (response: HabitCompletion) => this.onSuccess(response)
      });
    }
  }

  onSuccess(response: HabitCompletion): void {
    //TODO
    if(this.habit) {
      this.habit.completions = this.habit.completions + 1;
    }
  }

}
