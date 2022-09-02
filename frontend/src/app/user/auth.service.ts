import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthenticationRequest } from './dto/authentication-request';
import { AuthenticationResponse } from './dto/authentication-response';
import { RegistrationRequest } from './dto/registration-request';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) { }

  public authenticate(request: AuthenticationRequest):  Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiServerUrl}/authenticate`, request);
  }

  public register(request: RegistrationRequest):  Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiServerUrl}/register`, request);
  }
}
