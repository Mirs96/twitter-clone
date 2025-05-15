import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BookmarkDetails } from '../../model/tweet/bookmarkDetails';
import { ReplyDetails } from '../../model/reply/replyDetails';
import { ReplyService } from '../../model/reply/replyService';
import { UserService } from '../../model/authentication/userService';
import { LikeReplyDetails } from '../../model/reply/likeReplyDetails';
import { RouterModule } from '@angular/router';
import { HttpConfig } from '../../config/http-config';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-single-reply',
  imports: [RouterModule],
  providers: [DatePipe],
  templateUrl: './single-reply.component.html',
  styleUrl: './single-reply.component.css'
})
export class SingleReplyComponent implements OnInit {
  @Input({
    required: true
  })
  reply!: ReplyDetails;

  @Output()
  openReplyPopup = new EventEmitter<number>();

  @Output()
  loadNestedReplies = new EventEmitter<number>();

  likeDetails!: LikeReplyDetails;
  bookmarkDetails!: BookmarkDetails;
  userId!: number;
  showNestedReplies = false;
  baseUrl = HttpConfig.apiUrl.replace('/api', '');

  constructor(
    private replyService: ReplyService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.userId = Number(this.userService.getUserIdFromToken()) || 0;
  }

  formatDateTime(dateTimeString: string | undefined): string {
    if (!dateTimeString) {
      return '';
    }
    try {
      const date = new Date(dateTimeString);
      const formattedDate = this.datePipe.transform(date, 'yyyy-MM-dd');
      const formattedTime = this.datePipe.transform(date, 'HH:mm');
      return `${formattedDate} \u00B7 ${formattedTime}`;
    } catch (error) {
      console.error("Errore durante la formattazione della data:", error);
      return dateTimeString; // In caso di errore, mostra la stringa originale
    }
  }

  // Toggle like
  toggleLike() {
    this.likeDetails = {
      userId: this.userId,
      replyId: this.reply.id
    };

    if (!this.reply.liked) {
      this.replyService
        .addLikeToReply(this.likeDetails)
        .subscribe({
          next: r => {
            this.reply.liked = true;
            this.reply.likeCount = r.likeCount;
            this.reply.likeId = r.likeId;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    } else {
      this.replyService
        .removeLikeFromReply(this.reply.likeId)
        .subscribe({
          next: r => {
            this.reply.liked = false;
            this.reply.likeCount = r.likeCount;
            this.reply.likeId = undefined;
            this.cdr.detectChanges();
          },
          error: err => console.log(err)
        });
    }
  }

  getFullImageUrl(profilePicture: string) {
    return `${this.baseUrl}${profilePicture}`;
  }

  onReplyClick(): void {
    this.openReplyPopup.emit(this.reply.id);
  }

  toggleNestedRepliesVisibility(): void {
    this.loadNestedReplies.emit(this.reply.id);
    this.reply.showNested = !this.reply.showNested;
  }  
}
