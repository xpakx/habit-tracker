import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { Expedition } from './dto/expedition';

@Injectable({
  providedIn: 'root'
})
export class ExpeditionService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getActiveExpeditions(): Observable<Expedition[]> {
    return this.http.get<Expedition[]>(`${this.apiServerUrl}/expedition/active`, { headers: this.getHeaders() });
  }
}
