import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../model/authentication/authService';
import { RegisterDetails } from '../../model/authentication/RegisterDetails';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../model/authentication/userService';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;

  constructor(private fb: FormBuilder, 
    private authService: AuthService, 
    private router: Router,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.minLength(3)]],
      lastname: ['', [Validators.required, Validators.minLength(3)]],
      nickname: ['', [Validators.required, Validators.minLength(3)]],
      dob: ['', [Validators.required]],
      sex: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      phone: ['', [Validators.required, Validators.minLength(10)]],
      role: ['', [Validators.required]],
      profilePicture: [''],
      bio: [''],
    });
  }

  onSubmit(): void {
    console.log(this.registerForm.value);
    console.log(this.registerForm.valid);

    if (this.registerForm.valid) {
      const formData: RegisterDetails = {
        ...this.registerForm.value,
        creationDate: new Date().toISOString().split('T')[0]
      };
      console.log('Dati di registrazione: ', formData);

      this.authService.register(formData).subscribe({
        next: response => {
          alert('Registration successful');
          localStorage.setItem('jwtToken', response.token);
          this.userService.setLoggedIn(true);
          this.router.navigate(['/home']);          
        },
        error: err => {
          console.log(err);
          alert('Registration failed');
        }
      });
    } else {
      console.log('Form non valido.');
    }
  }
}
