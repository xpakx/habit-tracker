import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { AuthenticationResponse } from '../dto/authentication-response';
import { RegisterForm } from '../form/register-form';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  form: FormGroup<RegisterForm>;

  constructor(private authService: AuthService, private fb: FormBuilder, private router: Router) { 
    this.form = this.fb.nonNullable.group({
      username: [new String(''), Validators.required],
      password: [new String(''), Validators.required],
      passwordRe: [new String(''), Validators.required]
    });
  }

  ngOnInit(): void {
  }

  saveToken(response: AuthenticationResponse): void {
    localStorage.setItem("token", response.token);
    this.router.navigate(['/']);
  }

  showError(error: HttpErrorResponse): void {

  }

  register(): void {
    if(this.form.valid) {
      this.authService.register({ 
        username: this.form.controls.username.value, 
        password: this.form.controls.password.value,
        passwordRe: this.form.controls.passwordRe.value
      }).subscribe({
        next: (response: AuthenticationResponse) => this.saveToken(response),
        error: (error: HttpErrorResponse) => this.showError(error)
      });
    }
  }

}
