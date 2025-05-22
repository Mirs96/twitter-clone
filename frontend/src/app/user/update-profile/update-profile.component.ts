import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Compressor from 'compressorjs';
import { UserProfileService } from '../../model/user/userProfileService';
import { UserService } from '../../model/user/userService'; 
import { AuthService } from '../../model/authentication/authService'; 
import { HttpConfig } from '../../config/http-config';
import { UserProfileDetails } from '../../model/user/userProfileDetails';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-update-profile',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.css']
})
export class UpdateProfileComponent implements OnInit {
  @Output() profileUpdated = new EventEmitter<void>();
  @Output() cancelUpdate = new EventEmitter<void>();

  profileForm!: FormGroup;
  avatarPreview: string | ArrayBuffer | null = null;
  selectedAvatarFile: File | null = null;
  isProcessing = false;
  currentUserId: number | null = null;
  currentUserProfile: UserProfileDetails | null = null;

  constructor(
    private fb: FormBuilder,
    private userProfileService: UserProfileService,
    private userService: UserService, 
    private authService: AuthService 
  ) {}

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => {
      this.currentUserId = id;
      if (this.currentUserId) {
        this.loadProfileDetails(this.currentUserId);
      }
    });

    this.profileForm = this.fb.group({
      bio: ['', [Validators.maxLength(255)]],
      avatar: [null]
    });
  }

  loadProfileDetails(userId: number): void {
    this.userProfileService.getUserProfile(userId).subscribe({
        next: (profileData) => {
            this.currentUserProfile = profileData;
            this.profileForm.patchValue({ bio: profileData.bio || '' });
            if (profileData.profilePicture) {
                this.avatarPreview = `${HttpConfig.baseUrl}${profileData.profilePicture}`;
            } else {
                this.avatarPreview = null;
            }
        },
        error: (err) => console.error('Failed to load profile details', err)
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files?.[0]) {
      const originalFile = input.files[0];
      if (!originalFile.type.startsWith('image/')) {
        alert('Please select an image file.');
        return;
      }
      new Compressor(originalFile, {
        quality: 0.6, maxWidth: 800, maxHeight: 800,
        success: (compressedResult) => {
          this.selectedAvatarFile = new File([compressedResult], originalFile.name, { type: compressedResult.type || originalFile.type });
          const reader = new FileReader();
          reader.onloadend = () => { this.avatarPreview = reader.result as string; };
          reader.readAsDataURL(this.selectedAvatarFile);
          this.profileForm.get('avatar')?.markAsDirty(); // Mark as dirty for form state
        },
        error: (err) => {
          console.error('Compression error:', err.message);
          alert('Could not process image. Please try another one.');
          this.avatarPreview = this.currentUserProfile?.profilePicture ? `${HttpConfig.baseUrl}${this.currentUserProfile.profilePicture}` : null;
          this.selectedAvatarFile = null;
        },
      });
    } else {
        this.avatarPreview = this.currentUserProfile?.profilePicture ? `${HttpConfig.baseUrl}${this.currentUserProfile.profilePicture}` : null;
        this.selectedAvatarFile = null;
        this.profileForm.get('avatar')?.setValue(null);
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid || !this.currentUserId) return;
    if (!this.profileForm.dirty && !this.selectedAvatarFile) {
        this.profileUpdated.emit(); // No changes, just close
        return;
    }

    this.isProcessing = true;
    const formData = new FormData();
    formData.append('bio', this.profileForm.get('bio')?.value || '');
    if (this.selectedAvatarFile) {
      formData.append('avatar', this.selectedAvatarFile);
    }

    this.userProfileService.updateProfile(formData, this.currentUserId).subscribe({
      next: () => {
        if (this.currentUserId) {
            this.userService.fetchUserDetails(this.currentUserId).subscribe(); // Refresh global user details
        }
        this.profileUpdated.emit();
        this.isProcessing = false;
      },
      error: (err) => {
        console.error('Failed to update profile:', err);
        alert('Failed to update profile. Please try again.');
        this.isProcessing = false;
      }
    });
  }
}