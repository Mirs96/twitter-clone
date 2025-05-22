import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { TweetListComponent } from '../tweet/tweet-list/tweet-list.component';
import { AuthService } from '../model/authentication/authService';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-bookmarks',
  imports: [CommonModule, TweetListComponent],
  templateUrl: './bookmarks.component.html',
  styleUrl: './bookmarks.component.css'
})
export class BookmarksComponent implements OnInit, OnDestroy {
  userId: number | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.userId$
      .pipe(takeUntil(this.destroy$))
      .subscribe(id => {
        this.userId = id;
      });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  handleTweetUnbookmarked(tweetId: number): void {
    console.log(`Tweet ${tweetId} was unbookmarked from the Bookmarks page.`);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
