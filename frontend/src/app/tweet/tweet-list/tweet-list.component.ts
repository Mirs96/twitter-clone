import { ChangeDetectorRef, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { LikeTweetDetails } from '../../model/tweet/likeTweetDetails';
import { BookmarkDetails } from '../../model/tweet/bookmarkDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { UserService } from '../../model/authentication/userService';

@Component({
  selector: 'app-tweet-list',
  imports: [SingleTweetComponent],
  templateUrl: './tweet-list.component.html',
  styleUrl: './tweet-list.component.css'
})
export class TweetListComponent implements OnInit, OnChanges {
  tweets: DisplayTweetDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreTweets = true;
  likeDetails!: LikeTweetDetails;
  bookmarkDetails!: BookmarkDetails;
  currentUserId!: number;

  @Input({
    required: true
  })
  isFollowing!: boolean;
  
  @Input()
  userId!: number;

  constructor(
    private tweetService: TweetService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    setTimeout(() => {
      this.currentUserId = parseInt(this.userService.getUserIdFromToken() ?? "0");
      if (this.userId != null) {
        this.loadTweetsByUser();
      } else {
        this.loadTweets();
      }
    }, 1000);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userId']) {
      this.resetTweetList();
      this.loadTweetsByUser();
    } else if (changes['isFollowing']) {
      this.resetTweetList();
      this.loadTweets();
    }
  }

  resetTweetList(): void {
    this.page = 0;
    this.tweets = [];
    this.hasMoreTweets = true;
  }

  // Load tweets
  loadTweets(): void {
    if (this.isLoading || !this.hasMoreTweets) {
      return;
    }

    this.isLoading = true;

    if (!this.isFollowing) {
      this.tweetService
        .getTweets(this.page, this.size)
        .subscribe({
          next: r => this.handleTweetResponse(r),
          error: err => this.handleError(err)
        });
    } else { 
      // TODO
      console.log('following...');

      this.isLoading = false;
    }
  }

  loadTweetsByUser(): void {
    if (this.isLoading || !this.hasMoreTweets || this.userId == null) return;

    this.isLoading = true;
    this.tweetService.getTweetsByUserId(this.userId, this.page, this.size)
    .subscribe({
      next: r => this.handleTweetResponse(r),
      error: err => this.handleError(err)
    });
  }

  handleTweetResponse(r: any): void {
    this.tweets = [...this.tweets, ...r.content];
    this.page++;
    this.hasMoreTweets = this.page < r.totalPages;
    this.isLoading = false;
    setTimeout(() => {
      this.cdr.detectChanges();
    }, 100); // Ritardo di 100 millisecondi
  }

  handleError(err: any): void{
    console.log(err);
    this.isLoading = false;
  }

  // Do paging by scrolling down (scroll event)
  onScroll(event: any): void {
    const tolerance = 2; // Tolleranza di 2 pixel
    const difference = Math.abs(event.target.scrollHeight - (event.target.scrollTop + event.target.clientHeight));
    const isBottom = difference <= tolerance;

    if (isBottom) {
      this.loadTweets();
    }
  }
}