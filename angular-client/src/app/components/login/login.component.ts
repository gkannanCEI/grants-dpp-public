import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form: any = {
    username: '',
    password: '',
    role: 'Member'
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  isSignUpMode = false;
  successMessage = '';

  roles = ['CID Staff', 'Member', 'Sponsor'];

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.isLoggedIn = true;
      this.router.navigate(['/dashboard']);
    }
  }

  toggleMode(): void {
    this.isSignUpMode = !this.isSignUpMode;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onSubmit(): void {
    if (this.isSignUpMode) {
      this.onRegister();
    } else {
      this.onLogin();
    }
  }

  onLogin(): void {
    const { username, password } = this.form;

    this.authService.login(username, password).subscribe({
      next: data => {
        this.isLoggedIn = true;
        this.isLoginFailed = false;
        this.router.navigate(['/dashboard']);
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Login failed';
        this.isLoginFailed = true;
      }
    });
  }

  onRegister(): void {
    const { username, password, role } = this.form;

    this.authService.register(username, password, role).subscribe({
      next: data => {
        this.successMessage = 'Registration successful! You can now login.';
        this.isSignUpMode = false;
        this.isLoginFailed = false;
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Registration failed';
        this.isLoginFailed = true;
      }
    });
  }
}