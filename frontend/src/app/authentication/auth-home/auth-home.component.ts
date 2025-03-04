import { Component } from '@angular/core';
import { RegisterComponent } from '../register/register.component';
import { LoginComponent } from '../login/login.component';
import { UserService } from '../../model/authentication/userService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-home',
  imports: [RegisterComponent, LoginComponent],
  templateUrl: './auth-home.component.html',
  styleUrl: './auth-home.component.css'
})
export class AuthHomeComponent {
  isRegisterOpen = false;
  isLoginOpen = false;
  isLoggedIn = false;

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = this.userService.isLoggedIn();

    this.userService.loggedIn$.subscribe({
      next: s => this.isLoggedIn = s,
      error: err => console.log(err)
    });
  }

  openRegister() {
    this.isRegisterOpen = true;
  }

  closeRegister() {
    this.isRegisterOpen = false;
    if (this.userService.isLoggedIn()) { 
      this.router.navigateByUrl('/home');
    }
  }

  openLogin() {
    this.isLoginOpen = true;
  }

  closeLogin() {
    this.isLoginOpen = false;
    if (this.userService.isLoggedIn()) { 
      this.router.navigateByUrl('/home');
    }
  }

  logout() {
    alert(localStorage.getItem('jwtToken'));
    localStorage.removeItem('jwtToken');
  }

}
