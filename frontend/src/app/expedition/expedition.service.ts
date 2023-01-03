import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { ExpeditionSummary } from '../city/dto/expedition-summary';

@Injectable({
  providedIn: 'root'
})
export class ExpeditionService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getActiveExpeditions(): Observable<ExpeditionSummary[]> {
    return this.http.get<ExpeditionSummary[]>(`${this.apiServerUrl}/expedition/active`, { headers: this.getHeaders() });
  }
}
