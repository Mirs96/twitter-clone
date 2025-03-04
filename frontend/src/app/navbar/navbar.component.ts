import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../model/authentication/userService';

@Component({
  selector: 'app-navbar',
  imports: [RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  constructor(private userService: UserService) {}
  logout() {
    alert(localStorage.getItem('jwtToken'));
    localStorage.removeItem('jwtToken');
    this.userService.setLoggedIn(false);
  }
}
