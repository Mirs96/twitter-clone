import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SingleReplyComponent } from '../single-reply/single-reply.component';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';

@Component({
  selector: 'app-nested-reply',
  imports: [SingleReplyComponent],
  templateUrl: './nested-reply.component.html',
  styleUrl: './nested-reply.component.css'
})
export class NestedReplyComponent implements OnInit {
  nestedReplies!: ReplyDetails[];

  @Input()
  replyId!: number;

  @Output()
  openReplyPopup = new EventEmitter<number>();

  constructor(private replyService: ReplyService) { }

  ngOnInit(): void {
    this.loadNestedReplies(this.replyId);
  }

  // Load nested replies for a parent reply
  loadNestedReplies(replyId: number): void {
    this.replyService.getNestedRepliesByParentReplyId(replyId)
      .subscribe({
        next: r => {
          this.nestedReplies = r;
          // this.toggleShowNested(parentReplyId);
        },
        error: err => console.log(err)
      });
  }

  onReplyPopupOpened(replyId: number): void {
    this.openReplyPopup.emit(replyId);
  }


}
