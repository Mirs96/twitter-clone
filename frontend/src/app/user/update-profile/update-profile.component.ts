import { AfterViewInit, Component, ElementRef, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { UserProfileService } from '../../model/user/userProfileService';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../model/authentication/userService';
import Compressor from 'compressorjs';

@Component({
  selector: 'app-update-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.css']
})
export class UpdateProfileComponent implements OnInit, AfterViewInit {
  @ViewChild('profileInput')
  bioInput!: ElementRef<HTMLTextAreaElement>;

  @Output()
  profileUpdated = new EventEmitter<void>();

  profileForm!: FormGroup;
  avatarPreview: string | ArrayBuffer | null = null;
  userId!: number;
  selectedAvatarFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private userProfileService: UserProfileService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
    this.profileForm = this.fb.group({
      bio: ['', [Validators.maxLength(500)]]
    });
  }

  ngAfterViewInit(): void {
    this.adjustTextareaHeight();
  }

  adjustTextareaHeight(): void {
    if (!this.bioInput) {
      return;
    }
    const textarea = this.bioInput.nativeElement;
    textarea.style.height = '25px';
    textarea.style.height = `${textarea.scrollHeight}px`;
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
  
    if (input?.files?.[0]) {
      const file = input.files[0];
  
      if (!file.type.startsWith('image/')) {
        console.error('Il file selezionato non è un\'immagine');
        return; 
      }
  
      new Compressor(file, {
        quality: 0.6,  
        maxWidth: 800, 
        maxHeight: 800, 
        success: (compressedFile) => {
          if (compressedFile instanceof Blob) {
            this.handleCompressedFile(compressedFile, file);
          }
        },
        error: (err) => {
          console.error('Compression error:', err);
        }
      });
    }
  }
  
  private handleCompressedFile(compressedFile: Blob, originalFile: File): void {
    const fileWithName = new File([compressedFile], originalFile.name, { type: compressedFile.type });
  
    const reader = new FileReader();
    reader.onload = () => {
      this.avatarPreview = reader.result; 
    };
    reader.readAsDataURL(fileWithName);
  
    this.selectedAvatarFile = fileWithName; 
    
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      const formData = new FormData();
      formData.append('bio', this.profileForm.get('bio')?.value || '');
      if (this.selectedAvatarFile) {
        formData.append('avatar', this.selectedAvatarFile);
      }

      this.userProfileService.updateProfile(formData).subscribe({
        next: () => {
          this.profileUpdated.emit();
          this.resetForm();
        },
        error: (err) => console.error(err)
      });
    } else {
      console.log('Invalid form');
    }
  }

  onCancel(): void {
    this.profileUpdated.emit();
  }

  resetForm(): void {
    this.profileForm.reset();
    this.avatarPreview = null;
    this.selectedAvatarFile = null;
    setTimeout(() => this.adjustTextareaHeight(), 0); 
    this.profileUpdated.emit();
  }
}