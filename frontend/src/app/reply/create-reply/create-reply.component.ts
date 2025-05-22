import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';
import { AuthService } from '../../model/authentication/authService';
import { CreateReplyDetails } from '../../model/reply/createReplyDetails';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-reply',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './create-reply.component.html',
  styleUrl: './create-reply.component.css'
})
export class CreateReplyComponent implements OnInit, AfterViewInit {
  userId: number | null = null;
  replyForm!: FormGroup;
  
  @ViewChild('replyTextarea') replyTextarea!: ElementRef<HTMLTextAreaElement>;

  @Input() tweetId!: number;
  @Input() parentReplyId: number | null = null;
  @Output() replyCreated = new EventEmitter<ReplyDetails>();
  
  constructor(
    private replyService: ReplyService,
    private fb: FormBuilder,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => this.userId = id);
    this.replyForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  ngAfterViewInit(): void {
    if (this.replyTextarea) {
      this.adjustTextareaHeight();
    }
  }

  adjustTextareaHeight(): void {
    if (this.replyTextarea) {
      const textarea = this.replyTextarea.nativeElement;
      textarea.style.height = '25px'; 
      textarea.style.height = `${textarea.scrollHeight}px`;
    }
  }

  onSubmit(): void {
    if (this.replyForm.invalid || !this.userId) {
      console.error('Form invalid or user not logged in');
      return;
    }

    const payload: CreateReplyDetails = {
      ...this.replyForm.value,
      userId: this.userId,
      tweetId: this.tweetId,
      parentReplyId: this.parentReplyId
    };

    this.replyService.replyToTweet(payload).subscribe({
      next: newReply => {
        this.replyCreated.emit(newReply);
        this.replyForm.reset();
        this.adjustTextareaHeight();
      },
      error: err => console.error('Failed to post reply:', err)
    });
  }
}
