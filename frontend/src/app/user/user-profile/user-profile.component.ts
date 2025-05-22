import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserProfileDetails } from '../../model/user/userProfileDetails';
import { ReplyListComponent } from '../../reply/reply-list/reply-list.component';
import { TweetListComponent } from '../../tweet/tweet-list/tweet-list.component';
import { UpdateProfileComponent } from '../update-profile/update-profile.component';
import { HttpConfig } from '../../config/http-config';
import { UserProfileService } from '../../model/user/userProfileService';
import { AuthService } from '../../model/authentication/authService';

@Component({
  selector: 'app-user-profile',
  imports: [RouterModule, CommonModule, TweetListComponent, ReplyListComponent, UpdateProfileComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit {
  profile: UserProfileDetails | null = null;
  isLoading = true;
  error: string | null = null;
  activeTab: 'tweets' | 'replies' = 'tweets';
  showSetupProfileModal = false;
  listKey: string = '';
  profileUserId!: number;
  currentUserId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userProfileService: UserProfileService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.authService.userId$.subscribe(id => this.currentUserId = id);
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.profileUserId = Number(idParam);
        this.fetchProfile();
        this.activeTab = 'tweets';
        this.listKey = `user-${this.profileUserId}-tweets-${Date.now()}`;
      } else {
        this.error = 'User ID not provided.';
        this.isLoading = false;
      }
    });
  }

  fetchProfile(): void {
    if (!this.profileUserId) return;
    this.isLoading = true;
    this.error = null;
    this.userProfileService.getUserProfile(this.profileUserId).subscribe({
      next: data => {
        this.profile = data;
        this.isLoading = false;
      },
      error: err => {
        console.error("Failed to fetch profile:", err);
        this.error = 'Failed to load profile.';
        this.isLoading = false;
      }
    });
  }

  toggleFollow(): void {
    if (!this.profile || this.currentUserId === null || this.currentUserId === this.profileUserId) return;

    const wasFollowing = this.profile.isFollowing;
    // Optimistic update is tricky without knowing the new counts from backend immediately
    // For simplicity, we'll refetch or rely on backend to return updated profile if that's the API design
    const action = wasFollowing ? this.userProfileService.unfollow(this.profileUserId) : this.userProfileService.follow(this.profileUserId);
    
    action.subscribe({
        next: () => this.fetchProfile(), // Refetch to get updated state including counts
        error: (err) => {
            console.error('Failed to toggle follow:', err);
            alert('Could not update follow status.');
            // Optionally revert optimistic UI changes if any were made
        }
    });
  }

  handleProfileUpdated(): void {
     this.showSetupProfileModal = false;
     this.fetchProfile();
  }

  openSetupProfileModal(): void {
    this.showSetupProfileModal = true;
  }

  closeSetupProfileModal(): void {
    this.showSetupProfileModal = false;
  }

  switchTab(tab: 'tweets' | 'replies'): void {
      this.activeTab = tab;
      this.listKey = `user-${this.profileUserId}-${tab}-${Date.now()}`;
  }

  getFullImageUrl(profilePicturePath: string | null | undefined): string {
      if (!profilePicturePath) {
          return '/icons/default-avatar.png'; 
      }
      return `${HttpConfig.baseUrl}${profilePicturePath}`;
  };

  goBack(): void {
    this.router.navigate(['/home']);
  }
  
  noopReplyPopup(event: any): void{
    // Placeholder if UserProfile doesn't directly handle opening reply popups from its lists
  }
}
