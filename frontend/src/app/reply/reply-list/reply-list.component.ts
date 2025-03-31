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
  userId!: number;

  @Input({
    required: true
  })
  tweetId!: number;

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
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
    this.loadMainReplies();
  }

  // Add new reply
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['newReply'] && this.newReply) {
      this.addNewReply(this.newReply);
    }
  }

  // Load main replies for the tweet
  loadMainReplies(): void {
    if (this.isLoading || !this.hasMoreReplies) {
      return;
    }

    this.isLoading = true;
    this.replyService
      .getMainRepliesByTweetId(this.tweetId, this.page, this.size)
      .subscribe({
        next: r => {
          if (r.content.length === 0) {
            this.hasMoreReplies = false;
          } else {
            this.mainReplies = [...this.mainReplies, ...r.content];
            this.page++;
            this.hasMoreReplies = this.page < r.totalPages;
          }
          this.isLoading = false;
        },
        error: err => {
          console.log(err);
          this.isLoading = false;
        }
      });
  }



  /* toggleShowNested(replyId: number): void {
    let found = false;

    this.mainReplies.forEach(r => {
      if (r.id === replyId) {
        r.showNested = !r.showNested;
        found = true;
      }
    });

    if (!found) {
      this.nestedRepliesMap.forEach(nestedList => {
        nestedList.forEach(r => {
          if (r.id === replyId) {
            r.showNested = true;
          }
        });
      });
    }
  }
 */

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
      this.loadMainReplies();
    }
  }

  onReplyPopupOpened(replyId: number): void {
    this.openReplyPopup.emit(replyId);
  }
}
