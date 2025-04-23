import { Component, OnInit } from '@angular/core';
import { UserProfileDetails } from '../../model/user/userProfileDetails';
import { ActivatedRoute } from '@angular/router';
import { UserProfileService } from '../../model/user/userProfileService';
import { ReplyListComponent } from '../../reply/reply-list/reply-list.component';
import { TweetListComponent } from '../../tweet/tweet-list/tweet-list.component';
import { UserService } from '../../model/authentication/userService';

@Component({
  selector: 'app-user-profile',
  imports: [TweetListComponent, ReplyListComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit {
  profile!: UserProfileDetails;
  profileUserId!: number;
  currentUserId!: number; 
  isCurrentUserFollowing = false;
  activeTab: 'tweets' | 'replies' = 'tweets';
  
  constructor(
    private route: ActivatedRoute,
    private userProfileService: UserProfileService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.profileUserId = parseInt(this.route.snapshot.paramMap.get('id') ?? '0');
    
    this.userProfileService.getUserProfile(this.profileUserId)
      .subscribe(p => {
        this.profile = p;
        this.isCurrentUserFollowing = p.isFollowing;
      });
  }

  toggleFollow() {
    if (this.isCurrentUserFollowing) {
      this.userProfileService.unfollow(this.profileUserId)
        .subscribe(() => {
          this.isCurrentUserFollowing = false;
          this.profile.followersCount--;
        });
    } else {
      this.userProfileService.follow(this.profileUserId)
        .subscribe(() => {
          this.isCurrentUserFollowing = true;
          this.profile.followersCount++;
        });
    }
  }
}
