import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { ExperienceResponse } from './dto/experience-response';

@Injectable({
  providedIn: 'root'
})
export class GamificationService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { 
    super();
  }

  public getExperience():  Observable<ExperienceResponse> {
    return this.http.get<ExperienceResponse>(`${this.apiServerUrl}/experience`, { headers: this.getHeaders() });
  }
}
