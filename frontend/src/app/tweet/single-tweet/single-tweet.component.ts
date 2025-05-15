import { ChangeDetectorRef, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { LikeTweetDetails } from '../../model/tweet/likeTweetDetails';
import { BookmarkDetails } from '../../model/tweet/bookmarkDetails';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { UserService } from '../../model/authentication/userService';
import { HttpConfig } from '../../config/http-config';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-single-tweet',
  imports: [RouterModule],
  providers: [DatePipe],
  templateUrl: './single-tweet.component.html',
  styleUrl: './single-tweet.component.css'
})
export class SingleTweetComponent implements OnInit, OnChanges {
  @Input({
    required: true
  })
  tweet!: DisplayTweetDetails;

  @Input()
  replyCount!: number;

  likeDetails!: LikeTweetDetails;
  bookmarkDetails!: BookmarkDetails;
  userId!: number;
  baseUrl = HttpConfig.apiUrl.replace('/api', '');

  constructor(
    private tweetService: TweetService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['replyCount']) {
      this.tweet.replyCount = this.replyCount;
    }
  }

  formatDateTime(dateTimeString: string | undefined): string {
    if (!dateTimeString) {
      return '';
    }
    try {
      const date = new Date(dateTimeString);
      const formattedDate = this.datePipe.transform(date, 'yyyy-MM-dd');
      const formattedTime = this.datePipe.transform(date, 'HH:mm');
      return `${formattedDate} \u00B7 ${formattedTime}`;
    } catch (error) {
      console.error("Error during date formatting:", error);
      return dateTimeString;
    }
  }

  // Toggle like
  toggleLike() {
    this.likeDetails = {
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
      userId: this.userId,
      tweetId: this.tweet.id,
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

  getFullImageUrl(profilePicture: string) {
    return `${this.baseUrl}${profilePicture}`;
  }

  // Open a tweet
  openTweet(tweetId: number) {
    this.router.navigate(['/tweet', tweetId]);
  }
}
