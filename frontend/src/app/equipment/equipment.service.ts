import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { EquipmentResponse } from './dto/equipment-response';

@Injectable({
  providedIn: 'root'
})
export class EquipmentService  extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) {
    super();
  }

  public getEquipment(): Observable<EquipmentResponse> {
    return this.http.get<EquipmentResponse>(`${this.apiServerUrl}/equipment`, { headers: this.getHeaders() });
  }

  public getShips(): Observable<EquipmentResponse> {
    return this.http.get<EquipmentResponse>(`${this.apiServerUrl}/equipment/ship`, { headers: this.getHeaders() });
  }

  public getBuildings(): Observable<EquipmentResponse> {
    return this.http.get<EquipmentResponse>(`${this.apiServerUrl}/equipment/building`, { headers: this.getHeaders() });
  }
}
