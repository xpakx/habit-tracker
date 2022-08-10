import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ExperienceResponse } from './dto/experience-response';

@Injectable({
  providedIn: 'root'
})
export class GamificationService {
  private apiServerUrl = environment.gamificationServerUrl;

  constructor(private http: HttpClient) { }

  public getExperience(userId: number):  Observable<ExperienceResponse> {
    return this.http.get<ExperienceResponse>(`${this.apiServerUrl}/experience/${userId}`);
  }
}
