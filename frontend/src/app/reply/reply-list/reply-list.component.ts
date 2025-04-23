import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { SingleReplyComponent } from '../single-reply/single-reply.component';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';
import { UserService } from '../../model/authentication/userService';
import { NestedReplyComponent } from '../nested-reply/nested-reply.component';

@Component({
  selector: 'app-reply-list',
  imports: [SingleReplyComponent, NestedReplyComponent],
  templateUrl: './reply-list.component.html',
  styleUrl: './reply-list.component.css'
})
export class ReplyListComponent implements OnChanges {
  mainReplies: ReplyDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreReplies = true;
  currentUserId!: number;

  @Input()
  tweetId!: number;

  @Input()
  userId!: number;

  @Input()
  newReply!: ReplyDetails;

  @Output()
  openReplyPopup = new EventEmitter<number>();

  @Output()
  replyCreated = new EventEmitter<ReplyDetails>();

  constructor(
    private replyService: ReplyService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.currentUserId = Number(this.userService.getUserIdFromToken()) || 0;
    if (this.tweetId != null) {
      this.loadMainRepliesByTweet();
    } else if (this.userId != null) {
      this.loadRepliesByUser();
    }
  }

  // Add new reply
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['newReply'] && this.newReply) {
      this.addNewReply(this.newReply);
    }
  }

  // Load main replies for the tweet
  loadMainRepliesByTweet(): void {
    if (this.isLoading || !this.hasMoreReplies || this.tweetId === null) {
      return;
    }

    this.isLoading = true;
    this.replyService
      .getMainRepliesByTweetId(this.tweetId, this.page, this.size)
      .subscribe({
        next: r => this.handleReplyResponse(r),
        error: err => this.handleError(err)
      });
  }

  loadRepliesByUser(): void {
    if (this.isLoading || !this.hasMoreReplies || this.userId == null) return;

    this.isLoading = true;
    this.replyService
      .getRepliesByUserId(this.userId, this.page, this.size)
      .subscribe({
        next: r => {
          r.content.forEach(reply => {
            reply.hasNestedReplies = false; 
          });
          this.handleReplyResponse(r);
        },
        error: err => this.handleError(err)
      });
  }

  handleReplyResponse(r: any): void {
    if (r.content.length === 0) {
      this.hasMoreReplies = false;
    } else {
      this.mainReplies = [...this.mainReplies, ...r.content];
      this.page++;
      this.hasMoreReplies = this.page < r.totalPages;
    }
    this.isLoading = false;
  }

  handleError(err: any): void {
    console.log(err);
    this.isLoading = false;
  }

  addNewReply(newReply: ReplyDetails): void {
    this.mainReplies = [newReply, ...this.mainReplies];
    this.hasMoreReplies = true;
  }

  // Paging by scrolling down
  onScroll(event: any): void {
    const tolerance = 2; // Tolleranza di 2 pixel
    const difference = Math.abs(event.target.scrollHeight - (event.target.scrollTop + event.target.clientHeight));
    const isBottom = difference <= tolerance;

    if (isBottom) {
      this.loadMainRepliesByTweet();
    }
  }

  onReplyPopupOpened(replyId: number): void {
    this.openReplyPopup.emit(replyId);
  }
}
