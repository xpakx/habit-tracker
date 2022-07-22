import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Habit } from '../dto/habit';
import { HabitService } from '../habit.service';

@Component({
  selector: 'app-daily-view',
  templateUrl: './daily-view.component.html',
  styleUrls: ['./daily-view.component.css']
})
export class DailyViewComponent implements OnInit {
  habits: Habit[] = [];

  constructor(private habitService: HabitService) { }

  ngOnInit(): void {
    this.habitService.getDailyHabits().subscribe({
      next: (response: Habit[]) => this.updateHabitList(response),
      error: (error: HttpErrorResponse) => this.showError(error)

    });
  }

  updateHabitList(response: Habit[]): void {
    this.habits = response;
  }

  showError(error: HttpErrorResponse): void {
    //TODO
  }

}
