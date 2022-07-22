import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Habit } from './dto/habit';

@Injectable({
  providedIn: 'root'
})
export class HabitService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public getHabitsForDate():  Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiServerUrl}/habit/date`);
  }
}