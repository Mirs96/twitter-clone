import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../model/authentication/authService';
import { RegisterPayload } from '../../model/authentication/registerDetails';
import { Role } from '../../model/authentication/role';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  selectedFile: File | null = null;
  @Output() closeModal = new EventEmitter<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.minLength(3)]],
      lastname: ['', [Validators.required, Validators.minLength(3)]],
      nickname: ['', [Validators.required, Validators.minLength(3)]],
      dob: ['', Validators.required],
      sex: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      phone: ['', [Validators.required, Validators.minLength(10)]],
      role: ['', Validators.required],
      bio: ['', Validators.maxLength(500)],
      profilePicture: [null] 
    });
  }

  onFileChange(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    const fileList: FileList | null = element.files;
    if (fileList && fileList.length > 0) {
      this.selectedFile = fileList[0];
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const formValues = this.registerForm.value;
    const payload: RegisterPayload = {
        firstname: formValues.firstname,
        lastname: formValues.lastname,
        nickname: formValues.nickname,
        dob: formValues.dob,
        sex: formValues.sex,
        email: formValues.email,
        password: formValues.password,
        phone: formValues.phone,
        role: formValues.role as Role,
        bio: formValues.bio
    };

    let submissionData: RegisterPayload | FormData = payload;

    if (this.selectedFile) {
        const formData = new FormData();
        Object.keys(payload).forEach(key => {
            formData.append(key, (payload as any)[key]);
        });
        formData.append('profilePicture', this.selectedFile, this.selectedFile.name);
        submissionData = formData;
    }

    this.authService.register(submissionData).subscribe({
      next: response => {
        this.authService.setAuth(response.token);
        this.closeModal.emit();
        this.router.navigate(['/home']);
      },
      error: err => {
        console.error('Registration failed:', err);
        alert('Registration failed. Please try again.');
      }
    });
  }
}
