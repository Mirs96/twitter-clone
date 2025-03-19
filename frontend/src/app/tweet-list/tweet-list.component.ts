import { ChangeDetectorRef, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';
import { DisplayTweetDetails } from '../model/tweet/displayTweetDetails';
import { LikeTweetDetails } from '../model/tweet/likeTweetDetails';
import { BookmarkDetails } from '../model/tweet/bookmarkDetails';
import { TweetService } from '../model/tweet/tweetService';
import { UserService } from '../model/authentication/userService';
import { Router } from '@angular/router';

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
  userId!: number;

  @Input({
    required: true
  })
  isFollowing!: boolean;

  constructor(
    private tweetService: TweetService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userId = parseInt(this.userService.getUserIdFromToken() ?? "0");
    this.loadTweets();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isFollowing']) {
      this.page = 0; 
      this.tweets = [];  
      this.loadTweets();
    }
  }

  // Load tweets
  loadTweets(): void {
    if (this.isLoading || !this.hasMoreTweets) {
      return;
    }

    this.isLoading = true;
    console.log('loading');
    console.log(this.userId);

    if (!this.isFollowing) {
      this.tweetService
        .getGeneralTweets(this.page, this.size, this.userId)
        .subscribe({
          next: r => {
            this.tweets = [...this.tweets, ...r.content];
            this.page++;
            this.hasMoreTweets = this.page < r.totalPages;
            this.isLoading = false;
            setTimeout(() => {
              this.cdr.detectChanges();
            }, 100); // Ritardo di 100 millisecondi
          },
          error: err => {
            console.log(err);
            this.isLoading = false;
          }
        });
    } else { 
      // TODO
      console.log('following...');

      this.isLoading = false;
    }
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