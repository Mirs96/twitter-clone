import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TweetDetailComponent } from './tweet-detail.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('TweetDetailComponent', () => {
  let component: TweetDetailComponent;
  let fixture: ComponentFixture<TweetDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TweetDetailComponent, RouterTestingModule, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TweetDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
