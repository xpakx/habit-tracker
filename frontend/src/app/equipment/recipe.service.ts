import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { ItemResponse } from '../shop/dto/item-response';
import { CraftRequest } from './dto/craft-request';

@Injectable({
  providedIn: 'root'
})
export class RecipeService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) {
    super();
  }

  public craft(request: CraftRequest): Observable<ItemResponse> {
    return this.http.post<ItemResponse>(`${this.apiServerUrl}/craft`, request, { headers: this.getHeaders() });
  }
}
