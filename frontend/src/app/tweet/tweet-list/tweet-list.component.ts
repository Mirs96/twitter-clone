/* import { Component, Input, OnChanges, OnInit, SimpleChanges, OnDestroy, ViewChild, ElementRef, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { AuthService } from '../../model/authentication/authService';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-tweet-list',
  imports: [SingleTweetComponent, CommonModule],
  templateUrl: './tweet-list.component.html',
  styleUrl: './tweet-list.component.css'
})
export class TweetListComponent implements OnInit, OnChanges, OnDestroy, AfterViewInit {
  @Input() userId?: number;
  @Input() hashtagId?: number;
  @Input() listKey!: string; 
  @Input() fetchType!: 'trending' | 'following' | 'user' | 'bookmarks' | 'hashtag';
  @Output() tweetUnbookmarked = new EventEmitter<number>();

  tweets: DisplayTweetDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreTweets = true;
  error: string | null = null;
  private loggedInUserId: number | null = null;
  private intersectionObserver?: IntersectionObserver;
  private destroy$ = new Subject<void>();

  @ViewChild('tweetListContainer') tweetListContainer!: ElementRef;

  constructor(
    private tweetService: TweetService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.authService.userId$.pipe(takeUntil(this.destroy$)).subscribe(id => {
        this.loggedInUserId = id;
        this.resetAndLoadTweets(); 
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['listKey'] || changes['fetchType'] || changes['userId'] || changes['hashtagId']) {
        if(!changes['listKey']?.firstChange && !changes['fetchType']?.firstChange && !changes['userId']?.firstChange && !changes['hashtagId']?.firstChange){
             this.resetAndLoadTweets();
        }
    }
  }
  
  ngAfterViewInit(): void {
    this.setupIntersectionObserver();
  }

  resetAndLoadTweets(): void {
    this.tweets = [];
    this.page = 0;
    this.hasMoreTweets = true;
    this.error = null;
    this.isLoading = false;
    this.loadTweets(0);
  }

  loadTweets(currentPage: number): void {
    if (this.isLoading || (!this.hasMoreTweets && currentPage > 0)) return;
    if (currentPage === 0 && this.isLoading && this.tweets.length > 0) return; 

    this.isLoading = true;
    this.error = null;

    let observable;
    switch (this.fetchType) {
      case 'trending':
        observable = this.tweetService.getTweets(currentPage, this.size);
        break;
      case 'following':
        if (!this.loggedInUserId) {
          this.error = "Please log in to see tweets from users you follow.";
          this.handleLoadingCompletion();
          return;
        }
        observable = this.tweetService.getFollowedUsersTweets(currentPage, this.size);
        break;
      case 'user':
        if (this.userId === undefined) {
            this.error = 'User ID is required for user tweets.';
            this.handleLoadingCompletion();
            return;
        }
        observable = this.tweetService.getTweetsByUserId(this.userId, currentPage, this.size);
        break;
      case 'bookmarks':
        if (this.userId === undefined) { // Should be loggedInUserId for bookmarks
            this.error = 'User ID is required for bookmarks.';
            this.handleLoadingCompletion();
            return;
        }
        observable = this.tweetService.getBookmarkedTweets(this.userId, currentPage, this.size);
        break;
      case 'hashtag':
        if (this.hashtagId === undefined) {
            this.error = 'Hashtag ID is required for hashtag tweets.';
            this.handleLoadingCompletion();
            return;
        }
        observable = this.tweetService.getTweetsByHashtagId(this.hashtagId, currentPage, this.size);
        break;
      default:
        this.error = 'Invalid fetch type for tweets.';
        this.handleLoadingCompletion();
        return;
    }

    observable.pipe(takeUntil(this.destroy$)).subscribe({
      next: response => {
        if (!response || !response.content) {
            this.error = "Invalid API response structure";
            this.handleLoadingCompletion(false);
            return;
        }
        const morePagesExist = (currentPage + 1) < response.totalPages;
        this.hasMoreTweets = morePagesExist;

        if (response.content.length === 0 && currentPage === 0) {
          this.tweets = [];
        } else if (response.content.length > 0) {
          const existingIds = new Set(this.tweets.map(t => t.id));
          const newTweets = response.content.filter(t => !existingIds.has(t.id));
          this.tweets = currentPage === 0 ? response.content : [...this.tweets, ...newTweets];
          this.page = currentPage + 1;
        }
        this.isLoading = false;
        setTimeout(() => this.observeLastElement(), 0); 
      },
      error: err => {
        console.error('Failed to load tweets:', err);
        this.error = (err.message || 'Could not load tweets') + '. Please try again later.';
        this.handleLoadingCompletion(false);
      }
    });
  }

  private handleLoadingCompletion(hasMore = false): void {
    this.isLoading = false;
    this.hasMoreTweets = hasMore;
    if (!hasMore && this.tweets.length === 0 && !this.error) {
      // this.error = "No tweets to show"; // Or set a specific message
    }
  }

  setupIntersectionObserver(): void {
    const options = { root: this.tweetListContainer?.nativeElement, threshold: 0.5 };
    this.intersectionObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && this.hasMoreTweets && !this.isLoading) {
          this.loadTweets(this.page);
        }
      });
    }, options);
    this.observeLastElement();
  }

  observeLastElement(): void {
    if (this.intersectionObserver && this.tweetListContainer?.nativeElement) {
      this.intersectionObserver.disconnect();
      const lastElement = this.tweetListContainer.nativeElement.querySelector('.last-element');
      if (lastElement) {
        this.intersectionObserver.observe(lastElement);
      }
    }
  }

  onScroll(): void {
    // Manual scroll check can be a fallback or primary if IntersectionObserver is problematic
    if (this.tweetListContainer && !this.isLoading && this.hasMoreTweets) {
        const el = this.tweetListContainer.nativeElement;
        if (el.scrollHeight - el.scrollTop <= el.clientHeight + 100) { // 100px threshold
            this.loadTweets(this.page);
        }
    }
  }

  handleLocalBookmarkChange(event: { tweetId: number, isBookmarked: boolean }): void {
    if (this.fetchType === 'bookmarks' && !event.isBookmarked) {
      this.tweets = this.tweets.filter(t => t.id !== event.tweetId);
      this.tweetUnbookmarked.emit(event.tweetId);
    } else {
      this.tweets = this.tweets.map(t =>
        t.id === event.tweetId ? { ...t, bookmarked: event.isBookmarked, bookmarkId: event.isBookmarked ? t.bookmarkId || -1 : undefined } : t
      );
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.intersectionObserver) {
      this.intersectionObserver.disconnect();
    }
  }
}
 */

import { Component, Input, OnChanges, OnInit, SimpleChanges, OnDestroy, ViewChild, ElementRef, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { TweetService } from '../../model/tweet/tweetService';
import { AuthService } from '../../model/authentication/authService';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-tweet-list',
  imports: [SingleTweetComponent, CommonModule],
  templateUrl: './tweet-list.component.html',
  styleUrl: './tweet-list.component.css'
})
export class TweetListComponent implements OnInit, OnChanges, OnDestroy, AfterViewInit {
  @Input() userId?: number;
  @Input() hashtagId?: number;
  @Input() listKey!: string; 
  @Input() fetchType!: 'trending' | 'following' | 'user' | 'bookmarks' | 'hashtag';
  @Output() tweetUnbookmarked = new EventEmitter<number>(); // Used by BookmarksPageComponent

  tweets: DisplayTweetDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreTweets = true;
  error: string | null = null;
  private loggedInUserId: number | null = null;
  private intersectionObserver?: IntersectionObserver;
  private destroy$ = new Subject<void>();

  @ViewChild('tweetListContainer') tweetListContainer!: ElementRef;

  constructor(
    private tweetService: TweetService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.authService.userId$.pipe(takeUntil(this.destroy$)).subscribe(id => {
        this.loggedInUserId = id;
        if (this.listKey && this.fetchType) { // Ensure essential inputs are present
            this.resetAndLoadTweets(); 
        }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    let relevantChange = false;
    for (const propName in changes) {
      if (changes.hasOwnProperty(propName) && ['listKey', 'fetchType', 'userId', 'hashtagId'].includes(propName)) {
        if (!changes[propName].firstChange || propName === 'listKey') { // listKey change always triggers reload
          relevantChange = true;
          break;
        }
      }
    }

    if (relevantChange) {
        this.resetAndLoadTweets();
    }
  }
  
  ngAfterViewInit(): void {
    this.setupIntersectionObserver();
  }

  resetAndLoadTweets(): void {
    this.tweets = [];
    this.page = 0;
    this.hasMoreTweets = true;
    this.error = null;
    this.isLoading = false;
    this.loadTweets(0);
  }

  loadTweets(currentPage: number): void {
    if (this.isLoading || (!this.hasMoreTweets && currentPage > 0) ) return;
    
    this.isLoading = true;
    this.error = null;

    let observable;
    switch (this.fetchType) {
      case 'trending':
        observable = this.tweetService.getTweets(currentPage, this.size);
        break;
      case 'following':
        if (!this.loggedInUserId) {
          this.error = "Please log in to see tweets from users you follow.";
          this.handleLoadingCompletion(false, true);
          return;
        }
        observable = this.tweetService.getFollowedUsersTweets(currentPage, this.size);
        break;
      case 'user':
        if (this.userId === undefined) {
            this.error = 'User ID is required for user tweets.';
            this.handleLoadingCompletion(false, true);
            return;
        }
        observable = this.tweetService.getTweetsByUserId(this.userId, currentPage, this.size);
        break;
      case 'bookmarks':
        if (this.loggedInUserId === undefined || this.loggedInUserId === null) { 
            this.error = 'User ID is required for bookmarks.';
            this.handleLoadingCompletion(false, true);
            return;
        }
        observable = this.tweetService.getBookmarkedTweets(this.loggedInUserId, currentPage, this.size);
        break;
      case 'hashtag':
        if (this.hashtagId === undefined) {
            this.error = 'Hashtag ID is required for hashtag tweets.';
            this.handleLoadingCompletion(false, true);
            return;
        }
        observable = this.tweetService.getTweetsByHashtagId(this.hashtagId, currentPage, this.size);
        break;
      default:
        this.error = 'Invalid fetch type for tweets.';
        this.handleLoadingCompletion(false, true);
        return;
    }

    observable.pipe(takeUntil(this.destroy$)).subscribe({
      next: response => {
        if (!response || !response.content) {
            this.error = "Invalid API response structure";
            this.handleLoadingCompletion(false);
            return;
        }
        const morePagesExist = (currentPage + 1) < response.totalPages;
        this.hasMoreTweets = morePagesExist;

        if (response.content.length === 0 && currentPage === 0) {
          this.tweets = [];
        } else if (response.content.length > 0) {
          const existingIds = new Set(this.tweets.map(t => t.id));
          const newTweets = response.content.filter(t => !existingIds.has(t.id));
          this.tweets = currentPage === 0 ? response.content : [...this.tweets, ...newTweets];
          this.page = currentPage + 1;
        }
        this.isLoading = false;
        setTimeout(() => this.observeLastElement(), 0); 
      },
      error: err => {
        console.error('Failed to load tweets:', err);
        this.error = (err.message || 'Could not load tweets') + '. Please try again later.';
        this.handleLoadingCompletion(false);
      }
    });
  }

  private handleLoadingCompletion(hasMore = false, clearTweetsOnError = false): void {
    this.isLoading = false;
    this.hasMoreTweets = hasMore;
    if (clearTweetsOnError && this.error) {
        this.tweets = [];
    }
  }

  setupIntersectionObserver(): void {
    if (!this.tweetListContainer?.nativeElement) return;
    const options = { root: null, threshold: 0.5 }; 
    this.intersectionObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && this.hasMoreTweets && !this.isLoading) {
          this.loadTweets(this.page);
        }
      });
    }, options);
    this.observeLastElement();
  }

  observeLastElement(): void {
    if (this.intersectionObserver && this.tweetListContainer?.nativeElement) {
      this.intersectionObserver.disconnect(); 
      const lastElement = this.tweetListContainer.nativeElement.querySelector('.last-element');
      if (lastElement) {
        this.intersectionObserver.observe(lastElement);
      }
    }
  }

  onScroll(): void {
    // Fallback or primary scroll detection. Currently IntersectionObserver is primary.
  }

  handleLocalBookmarkChange(event: { tweetId: number, isBookmarked: boolean, newBookmarkCount: number, newBookmarkId?: number }): void {
    if (this.fetchType === 'bookmarks' && !event.isBookmarked) {
      this.tweets = this.tweets.filter(t => t.id !== event.tweetId);
      this.tweetUnbookmarked.emit(event.tweetId);
    } else {
      this.tweets = this.tweets.map(t =>
        t.id === event.tweetId ? { 
            ...t, 
            bookmarked: event.isBookmarked, 
            bookmarkId: event.isBookmarked ? event.newBookmarkId : undefined,
            bookmarkCount: event.newBookmarkCount // Update the count from the event
        } : t
      );
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.intersectionObserver) {
      this.intersectionObserver.disconnect();
    }
  }
}