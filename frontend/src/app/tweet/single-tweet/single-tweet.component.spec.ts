import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SingleTweetComponent } from './single-tweet.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SingleTweetComponent', () => {
  let component: SingleTweetComponent;
  let fixture: ComponentFixture<SingleTweetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleTweetComponent, RouterTestingModule, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SingleTweetComponent);
    component = fixture.componentInstance;
    // Mock input tweet before detectChanges
    component.tweet = {
        id: 1, userId:1, userNickname: 'test', userProfilePicture: '', content: 'test tweet',
        likeCount: 0, replyCount: 0, bookmarkCount: 0, liked: false, bookmarked: false,
        createdAt: new Date().toISOString()
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
