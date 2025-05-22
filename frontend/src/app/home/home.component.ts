import { Component, ElementRef, ViewChild, AfterViewInit, Renderer2 } from '@angular/core';
import { TweetListComponent } from '../tweet/tweet-list/tweet-list.component';
import { CreateTweetComponent } from '../tweet/create-tweet/create-tweet.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CreateTweetComponent, TweetListComponent, CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements AfterViewInit {
  activeTab: 'forYou' | 'following' = 'forYou';
  listKey: string = 'forYou';

  @ViewChild('homeContainer') homeContainer!: ElementRef;

  constructor(private renderer: Renderer2) { }

  ngAfterViewInit(): void {
    // Initial scroll setup or logic if needed
  }

  handleTabChange(tab: 'forYou' | 'following'): void {
    if (this.activeTab === tab) return;
    this.activeTab = tab;
    this.listKey = `${tab}-${Date.now()}`; // Force re-render of TweetList
    if (this.homeContainer.nativeElement) {
      this.homeContainer.nativeElement.scrollTop = 0;
    }
  }

  handleTweetCreated(): void {
    if (this.activeTab === 'forYou') {
        this.listKey = `forYou-${Date.now()}`; // Force re-fetch for 'For you' tab
    }
     // If on 'following' tab, new tweet won't appear unless user follows themselves, or logic is added
  }

  onScroll(): void {
    // Placeholder for scroll-based actions if needed, e.g., lazy loading header animations
  }
}
