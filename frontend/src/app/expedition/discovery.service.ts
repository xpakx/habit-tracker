import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { DiscoveryResponse } from './dto/discovery-response';
import { TreasureResponse } from './dto/treasure-response';

@Injectable({
  providedIn: 'root'
})
export class DiscoveryService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getTreasure(expeditionId: number): Observable<TreasureResponse> {
    return this.http.get<TreasureResponse>(`${this.apiServerUrl}/expedition/${expeditionId}/treasure`, { headers: this.getHeaders() });
  }

  public getIsland(expeditionId: number): Observable<DiscoveryResponse> {
    return this.http.get<DiscoveryResponse>(`${this.apiServerUrl}/expedition/${expeditionId}/island`, { headers: this.getHeaders() });
  }
}
