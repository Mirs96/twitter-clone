import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TweetListComponent } from '../tweet/tweet-list/tweet-list.component';
import { HashtagService } from '../model/hashtag/hashtagService';
import { TrendingHashtagDetails } from '../model/hashtag/trendingHashtagDetails';
import { Page } from '../model/page';
import { AuthService } from '../model/authentication/authService';
import { Subject } from 'rxjs';
import { takeUntil, switchMap, tap } from 'rxjs/operators';

@Component({
  selector: 'app-explore',
  imports: [RouterModule, CommonModule, TweetListComponent],
  templateUrl: './explore.component.html',
  styleUrl: './explore.component.css'
})
export class ExploreComponent implements OnInit, OnDestroy, AfterViewInit {
  hashtagId?: number;
  hashtagName?: string;
  isLoggedIn: boolean = false;

  trendingHashtags: TrendingHashtagDetails[] = [];
  hashtagPage = 0;
  isLoadingHashtags = false;
  hasMoreHashtags = true;
  errorHashtags: string | null = null;
  hashtagSize = 20;

  @ViewChild('exploreContainer') exploreContainer!: ElementRef;
  private intersectionObserver?: IntersectionObserver;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private hashtagService: HashtagService
  ) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$
        .pipe(takeUntil(this.destroy$))
        .subscribe(status => {
            this.isLoggedIn = status;
            this.route.paramMap.pipe(
                takeUntil(this.destroy$),
                tap(params => {
                    const idParam = params.get('hashtagId');
                    const nameParam = params.get('hashtagName');
                    this.hashtagId = idParam ? Number(idParam) : undefined;
                    this.hashtagName = nameParam ? decodeURIComponent(nameParam) : undefined;
                }),
                switchMap(() => this.resetAndLoadData())
            ).subscribe();
    });
  }

  resetAndLoadData() {
    this.trendingHashtags = [];
    this.hashtagPage = 0;
    this.hasMoreHashtags = true;
    this.errorHashtags = null;
    if (!this.hashtagId) {
        if (this.isLoggedIn) {
            this.loadTrendingHashtags(0);
        } else {
            this.errorHashtags = "Please log in to explore trends.";
            this.hasMoreHashtags = false;
        }
    }
    return []; // for switchMap
  }

  ngAfterViewInit(): void {
    if (!this.hashtagId) { // Only setup observer if not viewing a specific hashtag's tweets
        this.setupIntersectionObserver();
    }
  }

  loadTrendingHashtags(currentPage: number): void {
    if (!this.isLoggedIn && !this.hashtagId) {
      this.errorHashtags = "Please log in to explore trends.";
      this.handleLoadingCompletion(false);
      return;
    }
    if (this.isLoadingHashtags || (!this.hasMoreHashtags && currentPage !== 0)) return;
    this.isLoadingHashtags = true;
    this.errorHashtags = null;

    this.hashtagService.getPaginatedTrendingHashtags(currentPage, this.hashtagSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: Page<TrendingHashtagDetails>) => {
          if (!response || !response.content) {
            this.errorHashtags = "Invalid API response structure for trending hashtags";
            this.handleLoadingCompletion(false);
            return;
          }
          this.trendingHashtags = currentPage === 0 ? response.content : [...this.trendingHashtags, ...response.content.filter(newTag => !this.trendingHashtags.find(oldTag => oldTag.id === newTag.id))];
          this.hashtagPage = currentPage + 1;
          this.hasMoreHashtags = (currentPage + 1) < response.totalPages;
          this.isLoadingHashtags = false;
          setTimeout(() => this.observeLastHashtagElement(), 0);
        },
        error: (err: any) => {
          console.error('Failed to load trending hashtags:', err);
          if (err.status === 403) {
            this.errorHashtags = 'Access denied. You might need to log in or have specific permissions.';
          } else {
            this.errorHashtags = err.message || 'Could not load trending hashtags';
          }
          this.handleLoadingCompletion(false);
        }
      });
  }

  private handleLoadingCompletion(hasMore = false): void {
    this.isLoadingHashtags = false;
    this.hasMoreHashtags = hasMore;
  }

  setupIntersectionObserver(): void {
    const options = { root: this.exploreContainer?.nativeElement, threshold: 0.9 };
    this.intersectionObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && this.hasMoreHashtags && !this.isLoadingHashtags && this.isLoggedIn && !this.hashtagId) {
                this.loadTrendingHashtags(this.hashtagPage);
            }
        });
    }, options);
    this.observeLastHashtagElement();
  }

  observeLastHashtagElement(): void {
    if (this.intersectionObserver && this.exploreContainer?.nativeElement) {
        this.intersectionObserver.disconnect();
        const lastElement = this.exploreContainer.nativeElement.querySelector('.last-hashtag-element');
        if (lastElement) {
            this.intersectionObserver.observe(lastElement);
        }
    }
  }

  onScroll(): void {
    if (this.exploreContainer && !this.isLoadingHashtags && this.hasMoreHashtags && this.isLoggedIn && !this.hashtagId) {
        const el = this.exploreContainer.nativeElement;
        if (el.scrollHeight - el.scrollTop <= el.clientHeight + 100) { 
            this.loadTrendingHashtags(this.hashtagPage);
        }
    }
  }

  formatCount(count: number): string {
    if (count >= 1000000) return (count / 1000000).toFixed(1) + 'M posts';
    if (count >= 1000) return (count / 1000).toFixed(0) + 'K posts';
    return count + ' posts';
  }

  goBack(): void {
    if (this.hashtagId) {
      this.router.navigate(['/explore']);
    } else {
      this.router.navigate(['/home']);
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
