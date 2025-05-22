import { AfterViewInit, Component, ElementRef, OnInit, ViewChild, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateTweetDetails } from '../../model/tweet/createTweetDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { AuthService } from '../../model/authentication/authService';
import { UserService } from '../../model/user/userService';
import { HttpConfig } from '../../config/http-config';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-tweet',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './create-tweet.component.html',
  styleUrl: './create-tweet.component.css'
})
export class CreateTweetComponent implements OnInit, AfterViewInit {
  @ViewChild('tweetTextarea') tweetTextarea!: ElementRef<HTMLTextAreaElement>;
  @Output() tweetCreated = new EventEmitter<void>();
  
  tweetForm!: FormGroup;
  userId: number | null = null;
  profilePictureUrl: string = '/icons/default-avatar.png';

  constructor(
    private tweetService: TweetService,
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => this.userId = id);
    this.userService.userDetails$.subscribe(details => {
      this.profilePictureUrl = details?.profilePicture
        ? `${HttpConfig.baseUrl}${details.profilePicture}`
        : '/icons/default-avatar.png';
    });

    this.tweetForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  ngAfterViewInit(): void {
    this.adjustTextareaHeight();
  }

  adjustTextareaHeight(): void {
    if (this.tweetTextarea) {
        const textarea = this.tweetTextarea.nativeElement;
        textarea.style.height = 'auto'; 
        textarea.style.height = `${textarea.scrollHeight}px`;
    }
  }

  onSubmit(): void {
    if (this.tweetForm.invalid || !this.userId) {
        console.error('Form invalid or user not logged in');
        return;
    }

    const payload: CreateTweetDetails = {
      ...this.tweetForm.value,
      userId: this.userId
    };

    this.tweetService.createTweet(payload).subscribe({
      next: () => {
        this.tweetForm.reset();
        this.adjustTextareaHeight();
        this.tweetCreated.emit();
      },
      error: err => {
          console.error('Failed to post tweet:', err);
          alert('Could not post tweet. Please try again.');
      }
    });
  }
}
