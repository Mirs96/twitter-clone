import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowingTweetsComponent } from './following-tweet.component';

describe('FollowingTweetComponent', () => {
  let component: FollowingTweetsComponent;
  let fixture: ComponentFixture<FollowingTweetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowingTweetsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FollowingTweetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
