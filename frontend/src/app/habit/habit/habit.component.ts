import { Component, Input, OnInit } from '@angular/core';
import { Habit } from '../dto/habit';

@Component({
  selector: 'app-habit',
  templateUrl: './habit.component.html',
  styleUrls: ['./habit.component.css']
})
export class HabitComponent implements OnInit {
  @Input("habit") habit?: Habit;

  constructor() { }

  ngOnInit(): void {
  }

}
