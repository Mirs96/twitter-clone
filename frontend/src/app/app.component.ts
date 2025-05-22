import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { AuthHomeComponent } from './authentication/auth-home/auth-home.component';
import { RightSidebarComponent } from './right-sidebar/right-sidebar.component';
import { AuthService } from './model/authentication/authService';
import { UserService } from './model/user/userService';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, AuthHomeComponent, RightSidebarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  isLoggedIn = false;

  constructor(
    private authService: AuthService, 
    private userService: UserService,
    private cdRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authService.checkAuthStatus(); 
    this.authService.isLoggedIn$.subscribe(loggedInStatus => {
      this.isLoggedIn = loggedInStatus;
      if (this.isLoggedIn) {
        const userId = this.authService.getUserId();
        if (userId && (!this.userService.getUserDetailsValue() || this.userService.getUserDetailsValue()?.id !== userId)) {
          this.userService.fetchUserDetails(userId).subscribe();
        }
      } else {
        this.userService.clearUserState();
      }
      this.cdRef.detectChanges();
    });
  }
}
