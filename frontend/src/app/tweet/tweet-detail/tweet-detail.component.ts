import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../model/authentication/userService';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';

import { ReplyDetails } from '../../model/reply/replyDetails';
import { CreateReplyComponent } from '../../reply/create-reply/create-reply.component';
import { ReplyListComponent } from '../../reply/reply-list/reply-list.component';

@Component({
  selector: 'app-tweet-detail',
  imports: [SingleTweetComponent, CreateReplyComponent, ReplyListComponent],
  templateUrl: './tweet-detail.component.html',
  styleUrl: './tweet-detail.component.css'
})
export class TweetDetailComponent implements OnInit {
  tweet!: DisplayTweetDetails;
  tweetId!: number;
  userId!: number;
  newReply!: ReplyDetails;
  showReplyModal: boolean = false;
  parentReplyId: number | null = null;

  @Output()
  createdReply = new EventEmitter<ReplyDetails>();

  constructor(
    private tweetService: TweetService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.tweetId = Number(this.route.snapshot.paramMap.get('id'));
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
    this.loadTweetDetails();
  }

  // load tweet infomations
  loadTweetDetails(): void {
    this.tweetService.findTweetById(this.tweetId)
      .subscribe({
        next: r => {
          this.tweet = r;
          this.cdr.detectChanges();
        },
        error: err => console.log(err)
      });
  }

  // Reply to a reply: open a popup
  openReplyPopup(replyId: number): void {
    this.parentReplyId = replyId;
    this.showReplyModal = true;
  }

  // close popup
  closeReplyPopup(): void {
    this.showReplyModal = false;
    this.parentReplyId = null;
  }

  // propagate the new reply
  onCreatedReply(newReply: ReplyDetails): void {
    this.tweet.replyCount++;
    this.newReply = newReply;
    this.createdReply.emit(newReply);
    this.closeReplyPopup();
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }
}
