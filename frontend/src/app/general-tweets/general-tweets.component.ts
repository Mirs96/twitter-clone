import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { TweetService } from '../model/tweet/tweetService';
import { LikeTweetDetails } from '../model/tweet/likeTweetDetails';
import { UserService } from '../model/authentication/userService';
import { DisplayTweetDetails } from '../model/tweet/displayTweetDetails';

@Component({
  selector: 'app-general-tweets',
  imports: [],
  templateUrl: './general-tweets.component.html',
  styleUrl: './general-tweets.component.css'
})
export class GeneralTweetsComponent implements OnInit {
  tweets: DisplayTweetDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreTweets = true;
  likeDetails!: LikeTweetDetails;
  userId!: number;

  constructor(
    private tweetService: TweetService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.userId = parseInt(this.userService.getUserIdFromToken() ?? "0");
    this.loadTweets();
  }

  loadTweets(): void {
    if (this.isLoading || !this.hasMoreTweets) {
      return;
    }

    this.isLoading = true;
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
          this.isLoading = false;
        }
      });
  }

  onScroll(event: any): void {
    const tolerance = 2; // Tolleranza di 2 pixel
    const difference = Math.abs(event.target.scrollHeight - (event.target.scrollTop + event.target.clientHeight));
    const isBottom = difference <= tolerance;

    if (isBottom) {
      this.loadTweets();
    }
  }

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
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    } else {
      this.tweetService
        .removeLikeFromTweet(tweet.id)
        .subscribe({
          next: r => {
            tweet.liked = false;
            tweet.likeCount = r.likeCount;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    }
  }

  toggleBookmark(tweet: DisplayTweetDetails) {

  }
}
