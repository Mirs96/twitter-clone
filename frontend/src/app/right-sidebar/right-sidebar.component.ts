/* import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, takeUntil, catchError, filter } from 'rxjs/operators';
import { HashtagService } from '../model/hashtag/hashtagService';
import { SearchService } from '../model/search/searchService';
import { TrendingHashtagDetails } from '../model/hashtag/trendingHashtagDetails';
import { AutocompleteResponse, AutocompleteUser, AutocompleteHashtag } from '../model/search/autocompleteResponse';
import { HttpConfig } from '../config/http-config';

@Component({
  selector: 'app-right-sidebar',
  imports: [FormsModule, CommonModule],
  templateUrl: './right-sidebar.component.html',
  styleUrl: './right-sidebar.component.css'
})
export class RightSidebarComponent implements OnInit, OnDestroy {
  trendingHashtags: TrendingHashtagDetails[] = [];
  searchQuery: string = '';
  searchResults: AutocompleteResponse | null = null;
  isSearchLoading: boolean = false;
  showResults: boolean = false;
  
  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private hashtagService: HashtagService,
    private searchService: SearchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchTrendingHashtags();
    this.setupSearchDebounce();
  }

  fetchTrendingHashtags(): void {
    this.hashtagService.getTrendingHashtags()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: data => this.trendingHashtags = data,
        error: err => console.error('Failed to fetch trending hashtags:', err)
      });
  }

  setupSearchDebounce(): void {
    this.searchSubject.pipe(
      takeUntil(this.destroy$),
      filter(query => query.trim().length > 0),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        this.isSearchLoading = true;
        return this.searchService.autocompleteSearch(query).pipe(
            catchError(error => {
                console.error('Autocomplete search failed:', error);
                this.searchResults = null;
                this.isSearchLoading = false;
                return []; // Return empty array on error to prevent stream termination
            })
        );
      })
    ).subscribe({
        next: data => {
            this.searchResults = data;
            this.isSearchLoading = false;
        }
    });
  }

  onSearchChange(query: string): void {
    this.searchQuery = query;
    if (query.trim() === '') {
      this.searchResults = null;
      this.showResults = false;
      this.isSearchLoading = false;
    } else {
      this.showResults = true;
      this.searchSubject.next(query);
    }
  }

  setShowResults(show: boolean): void {
      this.showResults = show;
  }

  onSearchBlur(): void {
    setTimeout(() => this.showResults = false, 150); // Delay to allow click
  }

  handleResultClick(item: AutocompleteUser | AutocompleteHashtag, type: 'user' | 'hashtag'): void {
    this.searchQuery = '';
    this.searchResults = null;
    this.showResults = false;
    if (type === 'user') {
      this.router.navigate([`/profile/${(item as AutocompleteUser).id}`]);
    } else {
      const hashtagItem = item as AutocompleteHashtag;
      this.router.navigate([`/explore/tag/${hashtagItem.id}/${encodeURIComponent(hashtagItem.tag)}`]);
    }
  }
  
  navigateToHashtag(hashtag: TrendingHashtagDetails): void {
      this.router.navigate([`/explore/tag/${hashtag.id}/${encodeURIComponent(hashtag.tag)}`]);
  }

  getFullImageUrl(profilePicturePath?: string): string {
    if (!profilePicturePath) {
      return '/icons/default-avatar.png';
    }
    return `${HttpConfig.baseUrl}${profilePicturePath}`;
  }

  formatCount(count: number): string {
    if (count >= 1000000) return (count / 1000000).toFixed(1) + 'M posts';
    if (count >= 1000) return (count / 1000).toFixed(0) + 'K posts';
    return count + ' posts';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
 */

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, takeUntil, catchError, filter, map, tap, finalize } from 'rxjs/operators';
import { HashtagService } from '../model/hashtag/hashtagService';
import { SearchService } from '../model/search/searchService';
import { TrendingHashtagDetails } from '../model/hashtag/trendingHashtagDetails';
import { AutocompleteResponse, AutocompleteUser, AutocompleteHashtag } from '../model/search/autocompleteResponse';
import { HttpConfig } from '../config/http-config';

@Component({
  selector: 'app-right-sidebar',
  imports: [FormsModule, CommonModule],
  templateUrl: './right-sidebar.component.html',
  styleUrl: './right-sidebar.component.css'
})
export class RightSidebarComponent implements OnInit, OnDestroy {
  trendingHashtags: TrendingHashtagDetails[] = [];
  searchQuery: string = '';
  searchResults: AutocompleteResponse | null = null;
  isSearchLoading: boolean = false;
  showResults: boolean = false;
  
  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private hashtagService: HashtagService,
    private searchService: SearchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchTrendingHashtags();
    this.setupSearchDebounce();
  }

  fetchTrendingHashtags(): void {
    this.hashtagService.getTrendingHashtags()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: data => this.trendingHashtags = data,
        error: err => console.error('Failed to fetch trending hashtags:', err)
      });
  }

  setupSearchDebounce(): void {
    this.searchSubject.pipe(
      takeUntil(this.destroy$),
      map(query => query.trim()),       // 1. Trim the query immediately
      debounceTime(200),              // 2. Debounce (reduced from 300ms for better perceived first-letter responsiveness)
      distinctUntilChanged(),         // 3. Only proceed if the trimmed query has actually changed
      tap(trimmedQuery => {            // Handle UI for empty query after debounce/distinct
        if (trimmedQuery.length === 0) {
          this.searchResults = null;
          this.isSearchLoading = false;
          this.showResults = false;
        } else {
            // If we reached here with a non-empty query, results should be potentially shown
            // and loading will be set before the API call.
        }
      }),
      filter(trimmedQuery => trimmedQuery.length > 0), // 4. Filter out empty strings AFTER debounce and distinct
      tap(() => {
          this.isSearchLoading = true; // Set loading true right before switchMap
          this.showResults = true; // Ensure results overlay is shown
      }), 
      switchMap(trimmedQuery =>
        this.searchService.autocompleteSearch(trimmedQuery).pipe(
          catchError(error => {
            console.error('Autocomplete search failed:', error);
            this.searchResults = null; // Clear results on error
            return of(null as AutocompleteResponse | null); // Return null to keep stream alive
          }),
          finalize(() => this.isSearchLoading = false) // Always set loading to false when inner observable completes/errors
        )
      )
    ).subscribe(data => {
      this.searchResults = data; // Update results (could be null from catchError)
    });
  }

  onSearchChange(query: string): void {
    // this.searchQuery is updated by ngModel
    // Push the raw query to the subject; the stream will handle trimming.
    this.searchSubject.next(query);

    // Immediate UI update for showing/hiding results based on raw input,
    // stream will later refine searchResults and loading state.
    if (query.trim() === '') {
      this.showResults = false;
      // isSearchLoading and searchResults are handled by the stream when it processes the empty query
    } else {
      this.showResults = true; // Show results container immediately for non-empty input
    }
  }
  
  // This method is for the (focus) event in the template, but might not be strictly necessary
  // if onSearchChange and the stream handle showResults appropriately.
  // Keeping it for now as it was in the original structure.
  setShowResults(show: boolean): void {
      if (this.searchQuery.trim() !== '') { // Only show if there's actual text
        this.showResults = show;
      } else {
        this.showResults = false;
      }
  }

  onSearchBlur(): void {
    // Delay hiding results to allow click on items in the autocomplete list
    setTimeout(() => this.showResults = false, 150); 
  }

  handleResultClick(item: AutocompleteUser | AutocompleteHashtag, type: 'user' | 'hashtag'): void {
    this.searchQuery = ''; // Clear search input
    this.searchResults = null;
    this.showResults = false;
    this.searchSubject.next(''); // Push empty to reset stream state if needed

    if (type === 'user') {
      this.router.navigate([`/profile/${(item as AutocompleteUser).id}`]);
    } else {
      const hashtagItem = item as AutocompleteHashtag;
      this.router.navigate([`/explore/tag/${hashtagItem.id}/${encodeURIComponent(hashtagItem.tag)}`]);
    }
  }
  
  navigateToHashtag(hashtag: TrendingHashtagDetails): void {
      this.router.navigate([`/explore/tag/${hashtag.id}/${encodeURIComponent(hashtag.tag)}`]);
  }

  getFullImageUrl(profilePicturePath?: string): string {
    if (!profilePicturePath) {
      return '/icons/default-avatar.png';
    }
    return `${HttpConfig.baseUrl}${profilePicturePath}`;
  }

  formatCount(count: number): string {
    if (count >= 1000000) return (count / 1000000).toFixed(1) + 'M posts';
    if (count >= 1000) return (count / 1000).toFixed(0) + 'K posts';
    return count + ' posts';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}