import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NestedReplyComponent } from './nested-reply.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('NestedReplyComponent', () => {
  let component: NestedReplyComponent;
  let fixture: ComponentFixture<NestedReplyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NestedReplyComponent, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NestedReplyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
