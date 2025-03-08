import { Component } from '@angular/core';
import { CreateTweetComponent } from '../create-tweet/create-tweet.component';
import { FollowingTweetsComponent } from '../following-tweets/following-tweet.component';
import { GeneralTweetsComponent } from '../general-tweets/general-tweets.component';

@Component({
  selector: 'app-home',
  imports: [CreateTweetComponent, FollowingTweetsComponent, GeneralTweetsComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  // To display 'ForYou' tweets or 'Following' tweets
  isFollowing = false;

  onForYou(): void {
    this.isFollowing = false;
  }

  onFollowing(): void {
    this.isFollowing = true;
  }
}
