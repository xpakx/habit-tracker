import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError(
        (error: HttpErrorResponse) => {
          this.testAuthorization(error);
          return throwError(() => error);
        }
      )
    );
  }

  private testAuthorization(error: HttpErrorResponse): void {
    if (error.status === 401) {
      localStorage.removeItem("token");
      this.router.navigate(['login']);
    }
  }
}