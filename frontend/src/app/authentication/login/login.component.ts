import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginDetails } from '../../model/authentication/loginDetails';
import { AuthService } from '../../model/authentication/authService';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  @Output() closeModal = new EventEmitter<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const formData: LoginDetails = this.loginForm.value;

      this.authService.login(formData).subscribe({
        next: response => {
          this.authService.setAuth(response.token);
          this.closeModal.emit();
          this.router.navigate(['/home']);
        },
        error: err => {
          console.error('Login failed:', err);
          alert('Login failed. Check credentials and try again.');
        }
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}
