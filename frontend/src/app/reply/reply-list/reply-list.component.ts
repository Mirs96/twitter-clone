import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { SingleReplyComponent } from '../single-reply/single-reply.component';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';
import { NestedReplyComponent } from '../nested-reply/nested-reply.component';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-reply-list',
  imports: [SingleReplyComponent, NestedReplyComponent, CommonModule],
  templateUrl: './reply-list.component.html',
  styleUrl: './reply-list.component.css'
})
export class ReplyListComponent implements OnInit, OnChanges, OnDestroy {
  @Input() tweetId?: number;
  @Input() userId?: number; 
  @Input() newReply?: ReplyDetails | null;
  @Input() listKey!: string; // To help Angular detect changes and re-render/re-fetch
  @Output() openReplyPopup = new EventEmitter<number>();

  mainReplies: ReplyDetails[] = [];
  page = 0;
  size = 10;
  isLoading = false;
  hasMoreReplies = true;

  @ViewChild('repliesContainer') repliesContainer!: ElementRef;
  private destroy$ = new Subject<void>();

  constructor(private replyService: ReplyService) { }

  ngOnInit(): void {
    this.loadInitialReplies();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['newReply'] && this.newReply && !this.newReply.parentReplyId) {
      this.mainReplies = [this.newReply, ...this.mainReplies];
    }
    if (changes['listKey'] && !changes['listKey'].firstChange) {
      this.resetAndLoadReplies();
    }
    if ((changes['tweetId'] && !changes['tweetId'].firstChange) || (changes['userId'] && !changes['userId'].firstChange)){
      this.resetAndLoadReplies();
    }
  }

  resetAndLoadReplies(): void {
    this.mainReplies = [];
    this.page = 0;
    this.hasMoreReplies = true;
    this.isLoading = false;
    this.loadInitialReplies();
  }

  loadInitialReplies(): void {
    if (this.tweetId !== undefined || this.userId !== undefined) {
      this.loadReplies(0);
    }
  }

  loadReplies(currentPage: number): void {
    if (this.isLoading || !this.hasMoreReplies) return;
    this.isLoading = true;

    let observable;
    if (this.tweetId !== undefined) {
      observable = this.replyService.getMainRepliesByTweetId(this.tweetId, currentPage, this.size);
    } else if (this.userId !== undefined) {
      observable = this.replyService.getRepliesByUserId(this.userId, currentPage, this.size);
    } else {
      this.isLoading = false;
      return;
    }

    observable.pipe(takeUntil(this.destroy$)).subscribe({
      next: response => {
        const processedReplies = response.content.map(reply => ({
            ...reply,
            hasNestedReplies: reply.hasNestedReplies === undefined ? false : reply.hasNestedReplies,
            showNested: false
        }));

        if (response.content.length === 0 && currentPage === 0) {
          this.mainReplies = [];
          this.hasMoreReplies = false;
        } else if (response.content.length > 0) {
          this.mainReplies = currentPage === 0 ? processedReplies : [...this.mainReplies, ...processedReplies];
          this.page = currentPage + 1;
          this.hasMoreReplies = (currentPage + 1) < response.totalPages;
        } else {
            this.hasMoreReplies = false;
        }
        this.isLoading = false;
      },
      error: err => {
        console.error('Failed to load replies:', err);
        this.isLoading = false;
      }
    });
  }

  onScroll(): void {
    if (!this.repliesContainer || this.isLoading || !this.hasMoreReplies) return;
    const element = this.repliesContainer.nativeElement;
    const tolerance = 50;
    const isNearBottom = element.scrollHeight - element.scrollTop <= element.clientHeight + tolerance;

    if (isNearBottom) {
      this.loadReplies(this.page);
    }
  }

  onToggleNested(replyId: number): void {
     this.mainReplies = this.mainReplies.map(reply => 
        reply.id === replyId ? { ...reply, showNested: !reply.showNested } : reply
    );
  }

  onOpenReplyPopup(replyId: number): void {
    this.openReplyPopup.emit(replyId);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
