import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../model/authentication/authService';
import { UserService } from '../model/user/userService';
import { UserDetails } from '../model/user/userDetails';
import { HttpConfig } from '../config/http-config';

@Component({
  selector: 'app-navbar',
  imports: [RouterModule, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  userId: number | null = null;
  userDetails: UserDetails | null = null;
  profilePictureUrl: string = '/icons/default-avatar.png';

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => {
      this.userId = id;
    });

    this.userService.userDetails$.subscribe(details => {
      this.userDetails = details;
      this.profilePictureUrl = this.userDetails?.profilePicture
        ? `${HttpConfig.baseUrl}${this.userDetails.profilePicture}`
        : '/icons/default-avatar.png';
    });
  }

  handleLogout(): void {
    this.authService.clearAuth();
    this.userService.clearUserState();
    // Navigation to login/home will be handled by App component based on auth state
  }
}
