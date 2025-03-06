import { Component, OnInit } from '@angular/core';
import { TweetService } from '../model/tweet/tweetService';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-tweets',
  imports: [ReactiveFormsModule],
  templateUrl: './tweets.component.html',
  styleUrl: './tweets.component.css'
})
export class TweetsComponent {
  

}
