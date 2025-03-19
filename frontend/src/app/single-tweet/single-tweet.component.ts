import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { DisplayTweetDetails } from '../model/tweet/displayTweetDetails';
import { LikeTweetDetails } from '../model/tweet/likeTweetDetails';
import { TweetService } from '../model/tweet/tweetService';
import { BookmarkDetails } from '../model/tweet/bookmarkDetails';
import { Router } from '@angular/router';

@Component({
  selector: 'app-single-tweet',
  imports: [],
  templateUrl: './single-tweet.component.html',
  styleUrl: './single-tweet.component.css'
})
export class SingleTweetComponent {
  likeDetails!: LikeTweetDetails;
  bookmarkDetails!: BookmarkDetails;

  @Input({
    required: true
  })
  tweet!: DisplayTweetDetails;
  @Input({
    required: true
  })
  userId!: number;

  constructor(
    private tweetService: TweetService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  // Toggle like
  toggleLike() {
    this.likeDetails = {
      id: 0,
      userId: this.userId,
      tweetId: this.tweet.id
    };

    if (!this.tweet.liked) {
      this.tweetService
        .addLikeToTweet(this.likeDetails)
        .subscribe({
          next: r => {
            this.tweet.liked = true;
            this.tweet.likeCount = r.likeCount;
            this.tweet.likeId = r.likeId;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    } else {
      this.tweetService
        .removeLikeFromTweet(this.tweet.likeId)
        .subscribe({
          next: r => {
            this.tweet.liked = false;
            this.tweet.likeCount = r.likeCount;
            this.tweet.likeId = undefined;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    }
  }

  // Toggle bookmark
  toggleBookmark() {
    this.bookmarkDetails = {
      id: 0,
      userId: this.userId,
      tweetId: this.tweet.id,
      creationDate: new Date().toISOString().split('T')[0],
      creationTime: new Date().toLocaleTimeString("it-IT", { hour: "2-digit", minute: "2-digit" })
    };

    if (!this.tweet.bookmarked) {
      this.tweetService
        .addBookmarkToTweet(this.bookmarkDetails)
        .subscribe({
          next: r => {
            this.tweet.bookmarked = true;
            this.tweet.bookmarkCount = r.bookmarkCount;
            this.tweet.bookmarkId = r.bookmarkId;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    } else {
      this.tweetService
        .removeBookmarkFromTweet(this.tweet.bookmarkId)
        .subscribe({
          next: r => {
            this.tweet.bookmarked = false;
            this.tweet.bookmarkCount = r.bookmarkCount;
            this.tweet.bookmarkId = undefined;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    }
  }

  // Open a tweet
  openTweet(tweetId: number) {
    this.router.navigate(['/tweet', tweetId]);
  }
}
