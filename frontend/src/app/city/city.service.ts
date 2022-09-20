import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { Building } from './dto/building';
import { BuildingRequest } from './dto/building-request';
import { BuildingResponse } from './dto/building-response';
import { City } from './dto/city';

@Injectable({
  providedIn: 'root'
})
export class CityService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public build(request: BuildingRequest, cityId: number): Observable<BuildingResponse> {
    return this.http.post<BuildingResponse>(`${this.apiServerUrl}/city/${cityId}/building`, request, { headers: this.getHeaders() });
  }

  public getCities(): Observable<City[]> {
    return this.http.get<City[]>(`${this.apiServerUrl}/city/all`, { headers: this.getHeaders() });
  }

  public getBuildings(cityId: number): Observable<Building[]> {
    return this.http.get<Building[]>(`${this.apiServerUrl}/city/${cityId}/building`, { headers: this.getHeaders() });
  }
}
