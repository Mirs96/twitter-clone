import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleReplyComponent } from './single-reply.component';

describe('SingleReplyComponent', () => {
  let component: SingleReplyComponent;
  let fixture: ComponentFixture<SingleReplyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleReplyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SingleReplyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
