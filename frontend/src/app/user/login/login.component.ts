import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { AuthenticationResponse } from '../dto/authentication-response';
import { LoginForm } from '../form/login-form';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form: FormGroup<LoginForm>;

  constructor(private authService: AuthService, private fb: FormBuilder) { 
    this.form = this.fb.nonNullable.group({
      username: [new String(''), Validators.required],
      password: [new String(''), Validators.required]
    });
  }

  ngOnInit(): void {
  }

  saveToken(response: AuthenticationResponse): void {
    localStorage.setItem("token", response.token);
  }

  showError(error: HttpErrorResponse): void {

  }

  login(): void {
    if(this.form.valid) {
      this.authService.authenticate({ 
        username: this.form.controls.username.value, 
        password: this.form.controls.password.value
      }).subscribe({
        next: (response: AuthenticationResponse) => this.saveToken(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      });
    }
  }

}
