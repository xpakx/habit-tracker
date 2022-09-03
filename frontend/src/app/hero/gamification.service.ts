import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ExperienceResponse } from './dto/experience-response';

@Injectable({
  providedIn: 'root'
})
export class GamificationService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    let token = localStorage.getItem("token");
    return new HttpHeaders({'Authorization':`Bearer ${token}`});
  }

  public getExperience():  Observable<ExperienceResponse> {
    return this.http.get<ExperienceResponse>(`${this.apiServerUrl}/experience`, { headers: this.getHeaders() });
  }
}
