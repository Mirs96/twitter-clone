import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginDetails } from '../../model/authentication/LoginDetails';
import { AuthService } from '../../model/authentication/authService';
import { Router } from '@angular/router';
import { UserService } from '../../model/authentication/userService';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm!: FormGroup;

  constructor(private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSubmit(): void {
    console.log(this.loginForm.value);
    console.log(this.loginForm.valid);

    if (this.loginForm.valid) {
      const formData: LoginDetails = this.loginForm.value;
      console.log('Dati di login: ', formData);

      this.authService.login(formData).subscribe({
        next: response => {
          alert('Login successful');
          localStorage.setItem('jwtToken', response.token);
          this.userService.setLoggedIn(true);
          this.router.navigate(['/home']);
        },
        error: err => {
          console.log(err);
          alert('Login fallita, riprova');
        }
      });
    } else {
      console.log('Form non valido.');
    }
  }
}

