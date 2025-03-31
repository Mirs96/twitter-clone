import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';
import { UserService } from '../../model/authentication/userService';
import { CreateReplyDetails } from '../../model/reply/createReplyDetails';

@Component({
  selector: 'app-create-reply',
  imports: [ReactiveFormsModule],
  templateUrl: './create-reply.component.html',
  styleUrl: './create-reply.component.css'
})
export class CreateReplyComponent implements OnInit, AfterViewInit {
  userId!: number;
  newReply!: ReplyDetails;
  replyForm!: FormGroup;
  
  @ViewChild('tweetInput')
  replyInput!: ElementRef<HTMLTextAreaElement>;

  @Input({
    required: true
  })
  tweetId!: number;

  @Output()
  replyCreated = new EventEmitter<ReplyDetails>();
  
  @Input()
  parentReplyId: number | null = null;

  constructor(
    private replyService: ReplyService,
    private fb: FormBuilder,
    private userService: UserService,
  ) { }

  ngOnInit(): void {
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
    this.replyForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  ngAfterViewInit(): void {
    if (this.replyInput) {
      this.adjustTextareaHeight();
    }
  }

  adjustTextareaHeight(): void {
    if (this.replyInput) {
      const textarea = this.replyInput.nativeElement;
      textarea.style.height = '25px'; 
      textarea.style.height = `${textarea.scrollHeight}px`;
    }
  }

  onSubmit(): void {
    if (this.replyForm.valid) {
      const formData: CreateReplyDetails = {
        ...this.replyForm.value,
        userId: this.userId,
        tweetId: this.tweetId,
        parentReplyId: this.parentReplyId,
        creationDate: new Date().toISOString().split('T')[0],
        creationTime: new Date().toISOString().split('T')[1].slice(0, 5)
      }

      this.replyService
        .replyToTweet(formData)
        .subscribe({
          next: r => {
            this.newReply = r;
            this.replyCreated.emit(this.newReply);
            this.resetForm();
          },
          error: err => console.log(err)
        });
    } else {
      console.log('Invalid form');
    }
  }

  resetForm() {
    this.replyForm.reset();
    this.adjustTextareaHeight();
  }
}
