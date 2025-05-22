import { Component } from '@angular/core';
import { RegisterComponent } from '../register/register.component';
import { LoginComponent } from '../login/login.component';
import { AuthService } from '../../model/authentication/authService';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-home',
  imports: [RegisterComponent, LoginComponent, CommonModule],
  templateUrl: './auth-home.component.html',
  styleUrl: './auth-home.component.css'
})
export class AuthHomeComponent {
  isRegisterOpen = false;
  isLoginOpen = false;

  constructor(private authService: AuthService, private router: Router) { }

  openRegister(): void {
    this.isRegisterOpen = true;
  }

  closeRegister(): void {
    this.isRegisterOpen = false;
    if (this.authService.isLoggedIn()) { 
      this.router.navigate(['/home']);
    }
  }

  openLogin(): void {
    this.isLoginOpen = true;
  }

  closeLogin(): void {
    this.isLoginOpen = false;
    if (this.authService.isLoggedIn()) { 
      this.router.navigate(['/home']);
    }
  }
}
