import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TweetDetails } from '../../model/tweet/tweetDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { UserService } from '../../model/authentication/userService';

@Component({
  selector: 'app-create-tweet',
  imports: [ReactiveFormsModule],
  templateUrl: './create-tweet.component.html',
  styleUrl: './create-tweet.component.css'
})
export class CreateTweetComponent implements OnInit, AfterViewInit {
  @ViewChild('tweetInput')
  tweetInput!: ElementRef<HTMLTextAreaElement>;
  
  tweetForm!: FormGroup;
  userId!: number;
  tweet!: TweetDetails;

  constructor(
    private tweetService: TweetService,
    private fb: FormBuilder,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.userId = parseInt(this.userService.getUserIdFromToken() ?? "0");
    this.tweetForm = this.fb.group({
      content: ['', Validators.required]
    });
  }

  ngAfterViewInit() {
    this.adjustTextareaHeight(); // Regola altezza iniziale
  }

  adjustTextareaHeight() {
    const textarea = this.tweetInput.nativeElement;
    textarea.style.height = '25px';
    textarea.style.height = textarea.scrollHeight + 'px';
  }

  onSubmit(): void {
    if (this.tweetForm.valid && this.userId) {
      const formData: TweetDetails = {
        ...this.tweetForm.value,
        userId: this.userId
      };

      this.tweetService.createTweet(formData).subscribe({
        next: r => {
          this.tweet = r;
          this.resetForm();
        },
        error: err => console.log(err)
      });
    }
  }

  resetForm() {
    this.tweetForm.reset();
    this.adjustTextareaHeight();
  }
}
