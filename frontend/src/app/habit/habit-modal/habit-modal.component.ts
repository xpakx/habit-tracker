import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ContextService } from '../context.service';
import { ContextDetails } from '../dto/context-details';
import { Habit } from '../dto/habit';
import { HabitRequest } from '../dto/habit-request';
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
  contexts: ContextDetails[] = [];
  selectedContext?: number;

  constructor(private habitService: HabitService, private contextService: ContextService, private fb: FormBuilder) {
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
    this.contextService.getContexts().subscribe({
      next: (response: ContextDetails[]) => this.saveContexts(response)
    });
  }

  saveContexts(response: ContextDetails[]): void {
    this.contexts = response;
  }

  addHabit(): void {
    if(this.form.valid) {
      let request: HabitRequest = {
        name: this.form.controls.name.value,
        description: this.form.controls.description.value,
        interval: this.form.controls.interval.value,
        dailyCompletions: this.form.controls.dailyCompletions.value,
        start: this.form.controls.start.value,
        contextId: this.selectedContext,
        triggerName: this.form.controls.triggerName.value
      };

      this.habitService.addHabit(request).subscribe({
        next: (response: Habit) => this.onSuccess(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  onSuccess(response: Habit): void {
    //TODO
  }

  onError(error: HttpErrorResponse): void {
    //TODO
  }

  selectContext(id: number | undefined) {
    this.selectedContext = id;
  }
}
