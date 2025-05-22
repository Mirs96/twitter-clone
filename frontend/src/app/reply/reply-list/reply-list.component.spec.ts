import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReplyListComponent } from './reply-list.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ReplyListComponent', () => {
  let component: ReplyListComponent;
  let fixture: ComponentFixture<ReplyListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReplyListComponent, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReplyListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
