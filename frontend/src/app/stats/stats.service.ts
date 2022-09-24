import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { StatsResponse } from './dto/stats-response';

@Injectable({
  providedIn: 'root'
})
export class StatsService extends JwtService{
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getStats(): Observable<StatsResponse> {
    return this.http.get<StatsResponse>(`${this.apiServerUrl}/habit/stats`, { headers: this.getHeaders() });
  }

  public getStatsForContext(contextId: number): Observable<StatsResponse> {
    return this.http.get<StatsResponse>(`${this.apiServerUrl}/context/${contextId}/stats`, { headers: this.getHeaders() });
  }

  public getStatsForHabit(habitId: number): Observable<StatsResponse> {
    return this.http.get<StatsResponse>(`${this.apiServerUrl}/habit/${habitId}/stats`, { headers: this.getHeaders() });
  }
}
