import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ExpeditionSummary } from '../expedition/dto/expedition-summary';
import { JwtService } from '../common/jwt-service';
import { BattleResponse } from './dto/battle-response';
import { MoveResponse } from './dto/move-response';
import { MoveRequest } from './dto/mover-request';

@Injectable({
  providedIn: 'root'
})
export class BattleService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getBattle(expeditionId: number): Observable<BattleResponse> {
    return this.http.get<BattleResponse>(`${this.apiServerUrl}/expedition/${expeditionId}/battle"`, { headers: this.getHeaders() });
  }

  public prepare(request: MoveRequest, battleId: number): Observable<MoveResponse> {
    return this.http.post<MoveResponse>(`${this.apiServerUrl}/battle/${battleId}/position"`, request, { headers: this.getHeaders() });
  }

  public move(request: MoveRequest, battleId: number): Observable<MoveResponse> {
    return this.http.post<MoveResponse>(`${this.apiServerUrl}/battle/${battleId}/move"`, request, { headers: this.getHeaders() });
  }

  public endTurn(battleId: number): Observable<MoveResponse[]> {
    return this.http.post<MoveResponse[]>(`${this.apiServerUrl}/battle/${battleId}/turn/end"`, { headers: this.getHeaders() });
  }
}
