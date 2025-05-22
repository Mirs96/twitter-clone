import { Component, EventEmitter, Input, OnInit, Output, forwardRef } from '@angular/core';
import { SingleReplyComponent } from '../single-reply/single-reply.component';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nested-reply',
  imports: [SingleReplyComponent, CommonModule, forwardRef(() => NestedReplyComponent)],
  templateUrl: './nested-reply.component.html',
  styleUrl: './nested-reply.component.css'
})
export class NestedReplyComponent implements OnInit {
  @Input() parentReplyId!: number;
  @Output() openReplyPopup = new EventEmitter<number>();

  nestedReplies: ReplyDetails[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(private replyService: ReplyService) { }

  ngOnInit(): void {
    this.loadNestedReplies();
  }

  loadNestedReplies(): void {
    this.isLoading = true;
    this.error = null;
    this.replyService.getNestedRepliesByParentReplyId(this.parentReplyId).subscribe({
      next: replies => {
        this.nestedReplies = replies.map(r => ({ ...r, showNested: false }));
        this.isLoading = false;
      },
      error: err => {
        console.error('Failed to load nested replies:', err);
        this.error = 'Could not load replies.';
        this.isLoading = false;
      }
    });
  }

  onToggleNested(replyId: number): void {
    this.nestedReplies = this.nestedReplies.map(reply => 
        reply.id === replyId ? { ...reply, showNested: !reply.showNested } : reply
    );
  }
  
  onOpenReplyPopup(replyId: number): void {
    this.openReplyPopup.emit(replyId);
  }
}
