import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DisplayTweetDetails } from '../model/tweet/displayTweetDetails';
import { ReplyDetails } from '../model/reply/replyDetails';
import { CreateReplyDetails } from '../model/reply/createReplyDetails';
import { TweetService } from '../model/tweet/tweetService';
import { ReplyService } from '../model/reply/replyService';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../model/authentication/userService';
import { BookmarkDetails } from '../model/tweet/bookmarkDetails';
import { LikeTweetDetails } from '../model/tweet/likeTweetDetails';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';

@Component({
  selector: 'app-tweet-detail',
  imports: [ReactiveFormsModule, SingleTweetComponent],
  templateUrl: './tweet-detail.component.html',
  styleUrl: './tweet-detail.component.css'
})
export class TweetDetailComponent implements OnInit {
  tweet!: DisplayTweetDetails;
  replies: ReplyDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreReplies = true;
  tweetId!: number;
  userId!: number;
  newReply!: CreateReplyDetails;
  replyForm!: FormGroup;
  likeDetails!: LikeTweetDetails;
  bookmarkDetails!: BookmarkDetails;

  constructor(
    private tweetService: TweetService,
    private replyService: ReplyService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.tweetId = Number(this.route.snapshot.paramMap.get('id'));
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
    this.loadTweetDetails();
    this.loadReplies();

    this.replyForm = this.fb.group({
      content: ['', [Validators.required]]
    });
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

  // Load replies for the tweet
  loadReplies(): void {
    if (this.isLoading || !this.hasMoreReplies) return;

    this.isLoading = true;
    this.replyService
      .getReplyByTweet(this.tweetId, this.page, this.size)
      .subscribe({
        next: r => {
          if (r.content.length === 0) { // if there are no replies
            this.hasMoreReplies = false;
          } else {
            this.replies = [...this.replies, ...r.content];
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
  // Paging by scrolling down
  onScroll(event: any): void {
    const tolerance = 2; // Tolleranza di 2 pixel
    const difference = Math.abs(event.target.scrollHeight - (event.target.scrollTop + event.target.clientHeight));
    const isBottom = difference <= tolerance;

    if (isBottom) {
      this.loadReplies();
    }
  }

  onSubmit(): void {
    if (this.replyForm.valid) {
      const formData: CreateReplyDetails = {
        ...this.replyForm.value,
        userId: this.userId,
        tweetId: this.tweetId,
        creationDate: new Date().toISOString().split('T')[0],
        creationTime: new Date().toISOString().split('T')[1]
      }
      console.log('Dati di registrazione: ', formData);

      this.replyService
        .replyToTweet(formData)
        .subscribe({
          next: r => this.newReply = r,
          error: err => console.log(err)
        });
    } else {
      console.log('Invalid form');
    }
  }

  // Toggle like
  toggleLike(tweet: DisplayTweetDetails) {
    this.likeDetails = {
      id: 0,
      userId: this.userId,
      tweetId: tweet.id
    };

    if (!tweet.liked) {
      this.tweetService
        .addLikeToTweet(this.likeDetails)
        .subscribe({
          next: r => {
            tweet.liked = true;
            tweet.likeCount = r.likeCount;
            tweet.likeId = r.likeId;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    } else {
      this.tweetService
        .removeLikeFromTweet(tweet.likeId)
        .subscribe({
          next: r => {
            tweet.liked = false;
            tweet.likeCount = r.likeCount;
            tweet.likeId = undefined;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    }
  }

  // Toggle bookmark
  toggleBookmark(tweet: DisplayTweetDetails) {
    this.bookmarkDetails = {
      id: 0,
      userId: this.userId,
      tweetId: tweet.id,
      creationDate: new Date().toISOString().split('T')[0],
      creationTime: new Date().toLocaleTimeString("it-IT", { hour: "2-digit", minute: "2-digit" })
    };

    if (!tweet.bookmarked) {
      this.tweetService
        .addBookmarkToTweet(this.bookmarkDetails)
        .subscribe({
          next: r => {
            tweet.bookmarked = true;
            tweet.bookmarkCount = r.bookmarkCount;
            tweet.bookmarkId = r.bookmarkId;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    } else {
      this.tweetService
        .removeBookmarkFromTweet(tweet.bookmarkId)
        .subscribe({
          next: r => {
            tweet.bookmarked = false;
            tweet.bookmarkCount = r.bookmarkCount;
            tweet.bookmarkId = undefined;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    }
  }
}
