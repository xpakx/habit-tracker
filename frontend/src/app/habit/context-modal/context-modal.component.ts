import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ContextService } from '../context.service';
import { ContextRequest } from '../dto/context-request';
import { HabitContext } from '../dto/habit-context';

export interface ContextForm {
  name: FormControl<String>;
  description: FormControl<String>;
  timeBounded: FormControl<boolean>;
  activeStart: FormControl<Date>;
  activeEnd: FormControl<Date>;
}

@Component({
  selector: 'app-context-modal',
  templateUrl: './context-modal.component.html',
  styleUrls: ['./context-modal.component.css']
})
export class ContextModalComponent implements OnInit {
  form: FormGroup<ContextForm>;

  constructor(private fb: FormBuilder, private contextService: ContextService) {
    this.form = this.fb.nonNullable.group({
      name: [new String(''), Validators.required],
      description: [new String(''), Validators.required],
      timeBounded: [true],
      activeStart: [new Date()],
      activeEnd: [new Date()]
    });
   }

  ngOnInit(): void {
  }

  addContext(): void {
    if(this.form.valid) {
      let request: ContextRequest = {
        name: this.form.controls.name.value,
        description: this.form.controls.description.value,
        timeBounded: this.form.controls.timeBounded.value,
        activeStart: this.form.controls.activeStart.value,
        activeEnd: this.form.controls.activeEnd.value
      };

      this.contextService.addContext(request).subscribe({
        next: (response: HabitContext) => this.onSuccess(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  onSuccess(response: HabitContext): void {
    //TODO
  }

  onError(error: HttpErrorResponse): void {
    //TODO
  }
}
