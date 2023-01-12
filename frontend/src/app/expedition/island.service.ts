import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { IslandNameRequest } from './dto/island-name-request';
import { IslandNameResponse } from './dto/island-name-response';
import { IslandResponse } from './dto/island-reponse';

@Injectable({
  providedIn: 'root'
})
export class IslandService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getAllIslands(): Observable<IslandResponse> {
    return this.http.get<IslandResponse>(`${this.apiServerUrl}/island/all`, { headers: this.getHeaders() });
  }

  public renameIsland(islandId: number, request: IslandNameRequest): Observable<IslandNameResponse> {
    return this.http.post<IslandNameResponse>(`${this.apiServerUrl}/island/${islandId}/name`, request, { headers: this.getHeaders() });
  }

}
