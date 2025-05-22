import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SingleTweetComponent } from '../single-tweet/single-tweet.component';
import { CreateReplyComponent } from '../../reply/create-reply/create-reply.component';
import { ReplyListComponent } from '../../reply/reply-list/reply-list.component';
import { DisplayTweetDetails } from '../../model/tweet/displayTweetDetails';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { TweetService } from '../../model/tweet/tweetService';

@Component({
  selector: 'app-tweet-detail',
  imports: [RouterModule, CommonModule, SingleTweetComponent, CreateReplyComponent, ReplyListComponent],
  templateUrl: './tweet-detail.component.html',
  styleUrl: './tweet-detail.component.css'
})
export class TweetDetailComponent implements OnInit {
  tweet: DisplayTweetDetails | null = null;
  isLoading = true;
  error: string | null = null;
  newReplyForList: ReplyDetails | null = null;
  showReplyModal = false;
  parentReplyId: number | null = null;
  tweetId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tweetService: TweetService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.tweetId = Number(idParam);
      this.loadTweetDetails();
    } else {
      this.error = 'Tweet ID not provided.';
      this.isLoading = false;
    }
  }

  loadTweetDetails(): void {
    if (!this.tweetId) return;
    this.isLoading = true;
    this.error = null;
    this.tweetService.findTweetById(this.tweetId).subscribe({
      next: data => {
        this.tweet = data;
        this.isLoading = false;
      },
      error: err => {
        console.error("Failed to load tweet details:", err);
        this.error = 'Could not load tweet.';
        this.isLoading = false;
      }
    });
  }

  openReplyPopup(replyId: number): void {
    this.parentReplyId = replyId;
    this.showReplyModal = true;
  }

  closeReplyPopup(): void {
    this.showReplyModal = false;
    this.parentReplyId = null;
  }

  handleReplyCreated(createdReply: ReplyDetails): void {
    if (this.tweet) {
      this.tweet.replyCount = (this.tweet.replyCount || 0) + 1;
    }
    if (!createdReply.parentReplyId) {
      this.newReplyForList = { ...createdReply }; 
    }
    if (this.showReplyModal) {
      this.closeReplyPopup();
    }
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }
}
