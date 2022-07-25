import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { HabitService } from '../habit.service';

export interface HabitForm {
  name: FormControl<String>;
  description: FormControl<String>;
  interval: FormControl<number>;
  dailyCompletions: FormControl<number>;
  start: FormControl<Date>;
  triggerName: FormControl<String>;
}

@Component({
  selector: 'app-habit-modal',
  templateUrl: './habit-modal.component.html',
  styleUrls: ['./habit-modal.component.css']
})
export class HabitModalComponent implements OnInit {
  form: FormGroup<HabitForm>;

  constructor(private habitService: HabitService, private fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      name: [new String(''), Validators.required],
      description: [new String(''), Validators.required],
      interval: [1, Validators.required],
      dailyCompletions: [1, Validators.required],
      start: [new Date(), Validators.required],
      triggerName: [new String(''), Validators.required]
    });
   }
  

  ngOnInit(): void {
  }

  addHabit(): void {

  }
}
