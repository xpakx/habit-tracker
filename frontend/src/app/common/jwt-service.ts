import { HttpHeaders } from "@angular/common/http";

export abstract class JwtService {

  constructor() { }

  protected getHeaders(): HttpHeaders {
    let token = localStorage.getItem("token");
    return new HttpHeaders({'Authorization':`Bearer ${token}`});
  }
}
