import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ContextRequest } from './dto/context-request';
import { Habit } from './dto/habit';
import { HabitContext } from './dto/habit-context';

@Injectable({
  providedIn: 'root'
})
export class ContextService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public addContext(request: ContextRequest): Observable<HabitContext> {
    return this.http.post<HabitContext>(`${this.apiServerUrl}/context`, request);
  }

  public getDailyHabits(contextId: number):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/context/${contextId}/habit/daily`);
  }

  public getHabitsForDate(contextId: number, date: Date):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/context/${contextId}/habit/daily?date=${date}`);
  }

  public getHabitsForContext(contextId: number):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/context/${contextId}/habit`);
  }
}
