import { Component, ElementRef, HostListener, Renderer2, ViewChild } from '@angular/core';
import { CreateTweetComponent } from '../create-tweet/create-tweet.component';
import { FollowingTweetsComponent } from '../following-tweets/following-tweet.component';
import { GeneralTweetsComponent } from '../general-tweets/general-tweets.component';

@Component({
  selector: 'app-home',
  imports: [CreateTweetComponent, FollowingTweetsComponent, GeneralTweetsComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  isFollowing = false;
  @ViewChild('createTweet') createTweet!: ElementRef;
  @ViewChild('tweetListContainer') tweetListContainer!: ElementRef;

  constructor(private renderer: Renderer2) { }

  ngAfterViewInit(): void {
    this.onTweetListScroll();
  }

  onForYou(): void {
    this.isFollowing = false;
  }

  onFollowing(): void {
    this.isFollowing = true;
  }

  @HostListener('scroll', ['$event'])
  onTweetListScroll(event?: any) {
    if (this.createTweet && this.createTweet.nativeElement && this.tweetListContainer && this.tweetListContainer.nativeElement) {
      const scrollY = this.tweetListContainer.nativeElement.scrollTop;
      const creaPostElement = this.createTweet.nativeElement;
      const creaPostHeight = creaPostElement.offsetHeight;

      if (scrollY < creaPostHeight) {
        this.renderer.setStyle(creaPostElement, 'transform', `translateY(-${scrollY}px)`);
        this.renderer.setStyle(creaPostElement, 'opacity', '1');
      } else {
        this.renderer.setStyle(creaPostElement, 'opacity', '0');
      }
    }
  }
}
