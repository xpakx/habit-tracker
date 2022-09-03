import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { DateRequest } from './dto/date-request';
import { Habit } from './dto/habit';
import { HabitCompletion } from './dto/habit-completion';
import { HabitRequest } from './dto/habit-request';

@Injectable({
  providedIn: 'root'
})
export class HabitService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    let token = localStorage.getItem("token");
    return new HttpHeaders({'Authorization':`Bearer ${token}`});
  }

  public getDailyHabits():  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/habit/daily`, { headers: this.getHeaders() });
  }

  public getHabitsForDate(date: Date):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/habit/daily?date=${date}`, { headers: this.getHeaders() });
  }

  public addHabit(request: HabitRequest): Observable<Habit> {
    return this.http.post<Habit>(`${this.apiServerUrl}/habit`, request, { headers: this.getHeaders() });
  }

  public completeHabit(habitId: number, request: DateRequest): Observable<HabitCompletion> {
    return this.http.post<HabitCompletion>(`${this.apiServerUrl}/habit/${habitId}/completion`, request, { headers: this.getHeaders() });
  }
}
