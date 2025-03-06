import { Component } from '@angular/core';
import { TweetsComponent } from '../tweets/tweets.component';
import { CreateTweetComponent } from '../create-tweet/create-tweet.component';

@Component({
  selector: 'app-home',
  imports: [CreateTweetComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
