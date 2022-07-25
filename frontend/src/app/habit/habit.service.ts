import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Habit } from './dto/habit';
import { HabitRequest } from './dto/habit-request';

@Injectable({
  providedIn: 'root'
})
export class HabitService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getDailyHabits():  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/habit/daily`);
  }

  public getHabitsForDate(date: Date):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/habit/daily?date=${date}`);
  }

  public addHabit(request: HabitRequest): Observable<Habit> {
    return this.http.post<Habit>(`${this.apiServerUrl}/habit`, request);
  }
}
