import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ContextService } from '../context.service';
import { Habit } from '../dto/habit';

@Component({
  selector: 'app-context-view',
  templateUrl: './context-view.component.html',
  styleUrls: ['./context-view.component.css']
})
export class ContextViewComponent implements OnInit {
  habits: Habit[] = [];
  showHabitModal: boolean = false;
  errorMessage: String = '';
  errorOccured: boolean = false;

  constructor(private contextService: ContextService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      let page: number | undefined = routeParams['id'];
      if(page) {
        this.getHabits(routeParams['id']);
      } 
    }); 
  }

  getHabits(contextId: number) {
    this.contextService.getHabitsForContext(contextId).subscribe({
      next: (response: Habit[]) => this.updateHabitList(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  updateHabitList(response: Habit[]): void {
    this.habits = response;
  }

  showError(error: HttpErrorResponse): void {
    this.errorOccured = true;
    this.errorMessage = error.error.message;
  }

  displayHabitModal(): void {
    this.showHabitModal = true;
  }
}
