import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../model/authentication/userService';

@Component({
  selector: 'app-navbar',
  imports: [RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  userId!: number;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
  }

  logout() {
    alert(localStorage.getItem('jwtToken'));
    localStorage.removeItem('jwtToken');
    this.userService.setLoggedIn(false);
  }
}
