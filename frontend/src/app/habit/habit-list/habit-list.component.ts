import { Component, Input, OnInit } from '@angular/core';
import { Habit } from '../dto/habit';

@Component({
  selector: 'app-habit-list',
  templateUrl: './habit-list.component.html',
  styleUrls: ['./habit-list.component.css']
})
export class HabitListComponent implements OnInit {
  @Input("habits") habitList: Habit[] = [];

  constructor() { }

  ngOnInit(): void {
  }

}
