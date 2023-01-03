import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { ActionRequest } from './dto/action-request';
import { ActionResponse } from './dto/action-response';
import { ExpeditionResultResponse } from './dto/expedition-result-response';
import { ExpeditionSummary } from './dto/expedition-summary';

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

  public getResult(expeditionId: number): Observable<ExpeditionResultResponse> {
    return this.http.get<ExpeditionResultResponse>(`${this.apiServerUrl}/expedition/${expeditionId}/result`, { headers: this.getHeaders() });
  }

  public complete(request: ActionRequest, expeditionId: number): Observable<ActionResponse> {
    return this.http.post<ActionResponse>(`${this.apiServerUrl}/expedition/${expeditionId}/complete`, request, { headers: this.getHeaders() });
  }

  public return(request: ActionRequest, expeditionId: number): Observable<ActionResponse> {
    return this.http.post<ActionResponse>(`${this.apiServerUrl}/expedition/${expeditionId}/return`, request, { headers: this.getHeaders() });
  }
}
