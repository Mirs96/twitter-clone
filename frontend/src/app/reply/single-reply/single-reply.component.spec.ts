import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SingleReplyComponent } from './single-reply.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SingleReplyComponent', () => {
  let component: SingleReplyComponent;
  let fixture: ComponentFixture<SingleReplyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleReplyComponent, RouterTestingModule, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SingleReplyComponent);
    component = fixture.componentInstance;
    // Mock input reply before detectChanges
    component.reply = {
        id: 1, userId: 1, userNickname: 'test', userProfilePicture: '', tweetId: 1,
        content: 'test reply', likeCount: 0, liked: false, createdAt: new Date().toISOString()
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
