import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { ContextDetails } from './dto/context-details';
import { ContextRequest } from './dto/context-request';
import { Habit } from './dto/habit';
import { HabitContext } from './dto/habit-context';

@Injectable({
  providedIn: 'root'
})
export class ContextService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public addContext(request: ContextRequest): Observable<HabitContext> {
    return this.http.post<HabitContext>(`${this.apiServerUrl}/context`, request, { headers: this.getHeaders() });
  }

  public getDailyHabits(contextId: number):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/context/${contextId}/habit/daily`, { headers: this.getHeaders() });
  }

  public getHabitsForDate(contextId: number, date: Date):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/context/${contextId}/habit/daily?date=${date}`, { headers: this.getHeaders() });
  }

  public getHabitsForContext(contextId: number):  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/context/${contextId}/habit`, { headers: this.getHeaders() });
  }

  public getContexts(): Observable<ContextDetails[]> {
    return this.http.get<ContextDetails[]>(`${this.apiServerUrl}/context/all`, { headers: this.getHeaders() });
  }
}
