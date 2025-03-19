import { Component, ElementRef, EventEmitter, HostListener, Output, Renderer2, ViewChild } from '@angular/core';
import { CreateTweetComponent } from '../create-tweet/create-tweet.component';
import { TweetListComponent } from '../tweet-list/tweet-list.component';

@Component({
  selector: 'app-home',
  imports: [CreateTweetComponent, TweetListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  isFollowing = false;
  @ViewChild('createTweet') createTweet!: ElementRef;
  @ViewChild('tweetListContainer') tweetListContainer!: ElementRef;

  @Output()
  isFollowingChanged = new EventEmitter<boolean>();

  constructor(private renderer: Renderer2) { }

  toggleFollow(): void {
    this.isFollowing = !this.isFollowing;
    this.isFollowingChanged.emit(this.isFollowing);
  }

  ngAfterViewInit(): void {
    this.onTweetListScroll();
  }

  onForYou(): void {
    this.isFollowing = false;
    this.isFollowingChanged.emit(this.isFollowing); 
  }

  onFollowing(): void {
    this.isFollowing = true;
    this.isFollowingChanged.emit(this.isFollowing);
  }

  @HostListener('scroll', ['$event'])
  onTweetListScroll(event?: any) {
    if (this.createTweet && this.createTweet.nativeElement && this.tweetListContainer && this.tweetListContainer.nativeElement) {
      const scrollY = this.tweetListContainer.nativeElement.scrollTop;
      const createTweetElement = this.createTweet.nativeElement;
      const createTweetHeight = createTweetElement.offsetHeight;

      if (scrollY < createTweetHeight) {
        this.renderer.setStyle(createTweetElement, 'transform', `translateY(-${scrollY}px)`);
        this.renderer.setStyle(createTweetElement, 'opacity', '1');
      } else {
        this.renderer.setStyle(createTweetElement, 'opacity', '0');
      }
    }
  }
}
