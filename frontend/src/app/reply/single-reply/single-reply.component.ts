import { Component, EventEmitter, Input, OnInit, Output, OnChanges, SimpleChanges } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { HttpConfig } from '../../config/http-config';
import { ReplyService } from '../../model/reply/replyService';
import { AuthService } from '../../model/authentication/authService';
import { LikeReplyDetails } from '../../model/reply/likeReplyDetails';

@Component({
  selector: 'app-single-reply',
  imports: [RouterModule, CommonModule],
  providers: [DatePipe],
  templateUrl: './single-reply.component.html',
  styleUrl: './single-reply.component.css'
})
export class SingleReplyComponent implements OnInit, OnChanges {
  @Input({ required: true }) reply!: ReplyDetails;
  @Output() openReplyPopup = new EventEmitter<number>();
  @Output() toggleNested = new EventEmitter<number>();
  @Input() isNestedVisible?: boolean;

  localReply!: ReplyDetails;
  userId: number | null = null;

  constructor(
    private replyService: ReplyService,
    private authService: AuthService,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => this.userId = id);
    this.localReply = { ...this.reply };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['reply']) {
      this.localReply = { ...this.reply };
    }
    if (changes['isNestedVisible']) {
        this.isNestedVisible = changes['isNestedVisible'].currentValue;
    }
  }

  handleLikeToggle(): void {
    if (!this.userId) return;

    const currentLikeId = this.localReply.likeId;
    const currentlyLiked = this.localReply.liked;
    const currentLikeCount = this.localReply.likeCount;

    this.localReply.liked = !currentlyLiked;
    this.localReply.likeCount = currentlyLiked ? currentLikeCount - 1 : currentLikeCount + 1;
    this.localReply.likeId = currentlyLiked ? undefined : -1; // Placeholder

    if (!currentlyLiked) {
      const likeData: LikeReplyDetails = { userId: this.userId, replyId: this.localReply.id };
      this.replyService.addLikeToReply(likeData).subscribe({
        next: result => {
          this.localReply.liked = true;
          this.localReply.likeCount = result.likeCount;
          this.localReply.likeId = result.likeId;
        },
        error: () => {
          this.localReply.liked = currentlyLiked;
          this.localReply.likeCount = currentLikeCount;
          this.localReply.likeId = currentLikeId;
          alert("Could not update like status.");
        }
      });
    } else if (currentLikeId) {
      this.replyService.removeLikeFromReply(currentLikeId).subscribe({
        next: result => {
          this.localReply.liked = false;
          this.localReply.likeCount = result.likeCount;
          this.localReply.likeId = undefined;
        },
        error: () => {
          this.localReply.liked = currentlyLiked;
          this.localReply.likeCount = currentLikeCount;
          this.localReply.likeId = currentLikeId;
          alert("Could not update like status.");
        }
      });
    }
  }

  handleReplyClick(): void {
    this.openReplyPopup.emit(this.localReply.id);
  }

  handleToggleNestedClick(): void {
    this.toggleNested.emit(this.localReply.id);
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
      // Using date-fns formatting to match React exactly would require adding date-fns to Angular project
      // For now, using Angular's DatePipe which is similar
      const formattedDate = this.datePipe.transform(date, 'yyyy-MM-dd');
      const formattedTime = this.datePipe.transform(date, 'HH:mm');
      return `${formattedDate} \u00B7 ${formattedTime}`;
    } catch (error) {
      console.error("Error parsing date string:", error);
      return dateTimeString;
    }
  }
}
