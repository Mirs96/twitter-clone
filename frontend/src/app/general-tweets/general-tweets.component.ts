import { Component, OnInit } from '@angular/core';
import { TweetService } from '../model/tweet/tweetService';
import { TweetDetails } from '../model/tweet/tweetDetails';
import { Page } from '../model/page';

@Component({
  selector: 'app-general-tweets',
  imports: [],
  templateUrl: './general-tweets.component.html',
  styleUrl: './general-tweets.component.css'
})
export class GeneralTweetsComponent implements OnInit{
  tweets!: Page<TweetDetails>;
  page = 0;
  size = 10;
  constructor(private tweetService: TweetService) {}

  ngOnInit(): void {
    this.tweetService
        .getGeneralTweets(this.page, this.size)
        .subscribe({
          next: r  => this.tweets = r,
          error: err => console.log(err)
        });
  }

  reactToTweet() {

  }

  openComments(tweet: TweetDetails) {
  }

  addComment(tweet: TweetDetails, comment: string) {
  }
}
