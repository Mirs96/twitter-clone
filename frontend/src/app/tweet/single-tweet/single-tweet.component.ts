/* import { Component, Input, OnInit, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { HttpConfig } from '../../config/http-config';
import { TweetService } from '../../model/tweet/tweetService';
import { AuthService } from '../../model/authentication/authService';
import { LikeTweetDetails } from '../../model/tweet/likeTweetDetails';
import { BookmarkDetails } from '../../model/tweet/bookmarkDetails';

@Component({
  selector: 'app-single-tweet',
  imports: [RouterModule, CommonModule],
  providers: [DatePipe],
  templateUrl: './single-tweet.component.html',
  styleUrl: './single-tweet.component.css'
})
export class SingleTweetComponent implements OnInit, OnChanges {
  @Input({ required: true }) tweet!: DisplayTweetDetails;
  @Input() isDetailView: boolean = false;
  @Output() bookmarkChange = new EventEmitter<{ tweetId: number, isBookmarked: boolean }>();

  localTweet!: DisplayTweetDetails;
  userId: number | null = null;

  constructor(
    private tweetService: TweetService,
    private authService: AuthService,
    private router: Router,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => this.userId = id);
    this.localTweet = { ...this.tweet };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tweet']) {
      this.localTweet = { ...this.tweet };
    }
  }

  handleLikeToggle(): void {
    if (!this.userId) return;

    const currentLikeId = this.localTweet.likeId;
    const currentlyLiked = this.localTweet.liked;
    const currentLikeCount = this.localTweet.likeCount;

    this.localTweet.liked = !currentlyLiked;
    this.localTweet.likeCount = currentlyLiked ? currentLikeCount - 1 : currentLikeCount + 1;
    this.localTweet.likeId = currentlyLiked ? undefined : -1; // Placeholder

    if (!currentlyLiked) {
      const likeData: LikeTweetDetails = { userId: this.userId, tweetId: this.localTweet.id };
      this.tweetService.addLikeToTweet(likeData).subscribe({
        next: result => {
          this.localTweet.liked = true;
          this.localTweet.likeCount = result.likeCount;
          this.localTweet.likeId = result.likeId;
        },
        error: () => {
          this.localTweet.liked = currentlyLiked;
          this.localTweet.likeCount = currentLikeCount;
          this.localTweet.likeId = currentLikeId;
          alert("Could not update like status.");
        }
      });
    } else if (currentLikeId) {
      this.tweetService.removeLikeFromTweet(currentLikeId).subscribe({
        next: result => {
          this.localTweet.liked = false;
          this.localTweet.likeCount = result.likeCount;
          this.localTweet.likeId = undefined;
        },
        error: () => {
          this.localTweet.liked = currentlyLiked;
          this.localTweet.likeCount = currentLikeCount;
          this.localTweet.likeId = currentLikeId;
          alert("Could not update like status.");
        }
      });
    }
  }

  handleBookmarkToggle(): void {
    if (!this.userId) return;

    const currentBookmarkId = this.localTweet.bookmarkId;
    const currentlyBookmarked = this.localTweet.bookmarked;
    const currentBookmarkCount = this.localTweet.bookmarkCount;
    const newBookmarkedStatus = !currentlyBookmarked;

    this.localTweet.bookmarked = newBookmarkedStatus;
    this.localTweet.bookmarkCount = currentlyBookmarked ? currentBookmarkCount - 1 : currentBookmarkCount + 1;
    this.localTweet.bookmarkId = currentlyBookmarked ? undefined : -1; // Placeholder

    if (!currentlyBookmarked) {
      const bookmarkData: BookmarkDetails = { userId: this.userId, tweetId: this.localTweet.id };
      this.tweetService.addBookmarkToTweet(bookmarkData).subscribe({
        next: result => {
          this.localTweet.bookmarked = true;
          this.localTweet.bookmarkCount = result.bookmarkCount;
          this.localTweet.bookmarkId = result.bookmarkId;
          this.bookmarkChange.emit({ tweetId: this.localTweet.id, isBookmarked: true });
        },
        error: () => {
          this.localTweet.bookmarked = currentlyBookmarked;
          this.localTweet.bookmarkCount = currentBookmarkCount;
          this.localTweet.bookmarkId = currentBookmarkId;
          alert("Could not update bookmark status.");
        }
      });
    } else if (currentBookmarkId !== undefined) {
      this.tweetService.removeBookmarkFromTweet(currentBookmarkId).subscribe({
        next: result => {
          this.localTweet.bookmarked = false;
          this.localTweet.bookmarkCount = result.bookmarkCount;
          this.localTweet.bookmarkId = undefined;
          this.bookmarkChange.emit({ tweetId: this.localTweet.id, isBookmarked: false });
        },
        error: () => {
          this.localTweet.bookmarked = currentlyBookmarked;
          this.localTweet.bookmarkCount = currentBookmarkCount;
          this.localTweet.bookmarkId = currentBookmarkId;
          alert("Could not update bookmark status.");
        }
      });
    }
  }

  handleReplyClick(): void {
    if (!this.isDetailView) {
      this.router.navigate([`/tweet/${this.localTweet.id}`]);
    }
  }

  navigateToTweet(): void {
    if (!this.isDetailView) {
      this.router.navigate([`/tweet/${this.localTweet.id}`]);
    }
  }

  getFullImageUrl(profilePicturePath: string | null | undefined): string {
    if (!profilePicturePath) {
      return '/icons/default-avatar.png';
    }
    return `${HttpConfig.baseUrl}${profilePicturePath}`;
  }

  formatDateTime(dateTimeString: string | undefined): string {
    if (!dateTimeString) return '';
    try {
      const date = new Date(dateTimeString);
      const formattedDate = this.datePipe.transform(date, 'yyyy-MM-dd');
      const formattedTime = this.datePipe.transform(date, 'HH:mm');
      return `${formattedDate} \u00B7 ${formattedTime}`;
    } catch (error) {
      console.error("Error parsing date string:", error);
      return dateTimeString;
    }
  }
}
 */


import { Component, Input, OnInit, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { HttpConfig } from '../../config/http-config';
import { TweetService } from '../../model/tweet/tweetService';
import { AuthService } from '../../model/authentication/authService';
import { LikeTweetDetails } from '../../model/tweet/likeTweetDetails';
import { BookmarkDetails } from '../../model/tweet/bookmarkDetails';

@Component({
  selector: 'app-single-tweet',
  imports: [RouterModule, CommonModule],
  providers: [DatePipe],
  templateUrl: './single-tweet.component.html',
  styleUrl: './single-tweet.component.css'
})
export class SingleTweetComponent implements OnInit, OnChanges {
  @Input({ required: true }) tweet!: DisplayTweetDetails;
  @Input() isDetailView: boolean = false;
  @Output() bookmarkChange = new EventEmitter<{ tweetId: number, isBookmarked: boolean, newBookmarkCount: number, newBookmarkId?: number }>();

  localTweet!: DisplayTweetDetails;
  userId: number | null = null;

  constructor(
    private tweetService: TweetService,
    private authService: AuthService,
    private router: Router,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => this.userId = id);
    if (this.tweet) {
        this.localTweet = { ...this.tweet };
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tweet'] && this.tweet) {
      // Update localTweet if the input tweet changes.
      // This ensures that if the parent list updates the tweet data, this component reflects it.
      const currentLocalStateBookmarked = this.localTweet?.bookmarked;
      const currentLocalStateBookmarkId = this.localTweet?.bookmarkId;
      const currentLocalStateBookmarkCount = this.localTweet?.bookmarkCount;

      this.localTweet = { ...this.tweet };

      // Preserve optimistic updates if an API call is in flight (more complex scenario not fully handled here)
      // For simplicity, we assume parent's data is the source of truth when it changes.
      // If an operation just finished and parent list got updated, this ensures localTweet is also in sync.
    }
  }

  handleLikeToggle(): void {
    if (!this.userId || !this.localTweet) return;

    const originalState = {
        liked: this.localTweet.liked,
        likeCount: this.localTweet.likeCount,
        likeId: this.localTweet.likeId
    };

    this.localTweet.liked = !originalState.liked;
    this.localTweet.likeCount = originalState.liked ? originalState.likeCount - 1 : originalState.likeCount + 1;
    this.localTweet.likeId = originalState.liked ? undefined : -1;


    if (!originalState.liked) {
      const likeData: LikeTweetDetails = { userId: this.userId, tweetId: this.localTweet.id };
      this.tweetService.addLikeToTweet(likeData).subscribe({
        next: result => {
          this.localTweet.liked = true;
          this.localTweet.likeCount = result.likeCount;
          this.localTweet.likeId = result.likeId;
        },
        error: () => {
          this.localTweet.liked = originalState.liked;
          this.localTweet.likeCount = originalState.likeCount;
          this.localTweet.likeId = originalState.likeId;
          alert("Could not update like status.");
        }
      });
    } else if (originalState.likeId !== undefined) {
      this.tweetService.removeLikeFromTweet(originalState.likeId).subscribe({
        next: result => {
          this.localTweet.liked = false;
          this.localTweet.likeCount = result.likeCount;
          this.localTweet.likeId = undefined;
        },
        error: () => {
          this.localTweet.liked = originalState.liked;
          this.localTweet.likeCount = originalState.likeCount;
          this.localTweet.likeId = originalState.likeId;
          alert("Could not update like status.");
        }
      });
    }
  }

  handleBookmarkToggle(): void {
    if (!this.userId || !this.localTweet) return;

    const originalState = {
        bookmarked: this.localTweet.bookmarked,
        bookmarkCount: this.localTweet.bookmarkCount,
        bookmarkId: this.localTweet.bookmarkId
    };

    this.localTweet.bookmarked = !originalState.bookmarked;
    this.localTweet.bookmarkCount = originalState.bookmarked ? (originalState.bookmarkCount - 1) : (originalState.bookmarkCount + 1);
    this.localTweet.bookmarkId = originalState.bookmarked ? undefined : -1; // Optimistic: undefined for remove, placeholder for add


    if (!originalState.bookmarked) {
      const bookmarkData: BookmarkDetails = { userId: this.userId, tweetId: this.localTweet.id };
      this.tweetService.addBookmarkToTweet(bookmarkData).subscribe({
        next: result => {
          this.localTweet.bookmarked = true;
          this.localTweet.bookmarkCount = result.bookmarkCount;
          this.localTweet.bookmarkId = result.bookmarkId;
          this.bookmarkChange.emit({
            tweetId: this.localTweet.id,
            isBookmarked: true,
            newBookmarkCount: result.bookmarkCount,
            newBookmarkId: result.bookmarkId
          });
        },
        error: () => {
          this.localTweet.bookmarked = originalState.bookmarked;
          this.localTweet.bookmarkCount = originalState.bookmarkCount;
          this.localTweet.bookmarkId = originalState.bookmarkId;
          alert("Could not update bookmark status.");
        }
      });
    } else if (originalState.bookmarkId !== undefined) {
      this.tweetService.removeBookmarkFromTweet(originalState.bookmarkId).subscribe({
        next: result => {
          this.localTweet.bookmarked = false;
          this.localTweet.bookmarkCount = result.bookmarkCount;
          this.localTweet.bookmarkId = undefined;
          this.bookmarkChange.emit({
            tweetId: this.localTweet.id,
            isBookmarked: false,
            newBookmarkCount: result.bookmarkCount
          });
        },
        error: () => {
          this.localTweet.bookmarked = originalState.bookmarked;
          this.localTweet.bookmarkCount = originalState.bookmarkCount;
          this.localTweet.bookmarkId = originalState.bookmarkId;
          alert("Could not update bookmark status.");
        }
      });
    }
  }

  handleReplyClick(): void {
    if (!this.isDetailView && this.localTweet) {
      this.router.navigate([`/tweet/${this.localTweet.id}`]);
    }
  }

  navigateToTweet(): void {
    if (!this.isDetailView && this.localTweet) {
      this.router.navigate([`/tweet/${this.localTweet.id}`]);
    }
  }

  getFullImageUrl(profilePicturePath: string | null | undefined): string {
    if (!profilePicturePath) {
      return '/icons/default-avatar.png';
    }
    return `${HttpConfig.baseUrl}${profilePicturePath}`;
  }

  formatDateTime(dateTimeString: string | undefined): string {
    if (!dateTimeString) return '';
    try {
      const date = new Date(dateTimeString);
      const formattedDate = this.datePipe.transform(date, 'yyyy-MM-dd');
      const formattedTime = this.datePipe.transform(date, 'HH:mm');
      return `${formattedDate} \u00B7 ${formattedTime}`;
    } catch (error) {
      console.error("Error parsing date string:", error);
      return dateTimeString;
    }
  }
}