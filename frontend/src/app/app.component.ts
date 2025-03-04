import { ChangeDetectorRef, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { AuthHomeComponent } from './authentication/auth-home/auth-home.component';
import { UserService } from './model/authentication/userService';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './authentication/register/register.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, AuthHomeComponent, HomeComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  isLoggedIn = false;

  constructor(private userService: UserService, private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.isLoggedIn = this.userService.isLoggedIn();
    
    this.userService.loggedIn$.subscribe({
      next: s => {
        this.isLoggedIn = s;
        console.log('Stato login aggiornato:', this.isLoggedIn);
        this.cdRef.detectChanges();
      },
      error: err => console.log(err)
    });
  }
}
