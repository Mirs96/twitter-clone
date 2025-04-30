import { Component, OnInit } from '@angular/core';
import { UserProfileDetails } from '../../model/user/userProfileDetails';
import { ActivatedRoute, Router } from '@angular/router';
import { UserProfileService } from '../../model/user/userProfileService';
import { ReplyListComponent } from '../../reply/reply-list/reply-list.component';
import { TweetListComponent } from '../../tweet/tweet-list/tweet-list.component';
import { UpdateProfileComponent } from '../update-profile/update-profile.component';
import { UserService } from '../../model/authentication/userService';

@Component({
  selector: 'app-user-profile',
  imports: [TweetListComponent, ReplyListComponent, UpdateProfileComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit {
  profile!: UserProfileDetails;
  profileUserId!: number;
  currentUserId!: number;
  isCurrentUserFollowing = false;
  activeTab: 'tweets' | 'replies' = 'tweets';
  showSetupProfileModal: boolean = false;
  baseUrl = "http://localhost:8080";

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private userProfileService: UserProfileService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.profileUserId = parseInt(this.route.snapshot.paramMap.get('id') ?? '0');
    this.currentUserId = Number(this.userService.getUserIdFromToken()) || 0;
    this.userProfileService.getUserProfile(this.profileUserId)
      .subscribe(p => {
        this.profile = p;
        this.isCurrentUserFollowing = p.isFollowing;
      });
  }

  getFullImageUrl(profilePicture: string) {
    return `${this.baseUrl}${profilePicture}`;
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

  onUpdatedProfile(): void {
    this.userProfileService.getUserProfile(this.profileUserId).subscribe((profile) => {
      this.profile = profile;
      this.closeSetupProfilePopup();
    });
  }

  openSetupProfilePopup(): void {
    console.log(this.showSetupProfileModal);
    this.showSetupProfileModal = true;
  }

  closeSetupProfilePopup(): void { 
    this.showSetupProfileModal = false;
  }
  
  goHome(): void {
    this.router.navigate(['/home']);
  }
}
