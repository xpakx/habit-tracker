import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtService } from '../common/jwt-service';
import { BuyRequest } from './dto/buy-request';
import { Shop } from './dto/shop';
import { ShopResponse } from './dto/shop-response';

@Injectable({
  providedIn: 'root'
})
export class ShopService extends JwtService {
  private apiServerUrl = environment.apiServerUrl;

  constructor(private http: HttpClient) {
    super();
  }

  public getShop(shopId: number): Observable<ShopResponse> {
    return this.http.get<ShopResponse>(`${this.apiServerUrl}/shop/${shopId}`, { headers: this.getHeaders() });
  }

  public buy(request: BuyRequest, entryId: number): Observable<ShopResponse> {
    return this.http.post<ShopResponse>(`${this.apiServerUrl}/shop/item/${entryId}`, request, { headers: this.getHeaders() });
  }

  public getShops(): Observable<Shop[]> {
    return this.http.get<Shop[]>(`${this.apiServerUrl}/shop/all`, { headers: this.getHeaders() });
  }

}
