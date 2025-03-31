import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NestedReplyComponent } from './nested-reply.component';

describe('NestedReplyComponent', () => {
  let component: NestedReplyComponent;
  let fixture: ComponentFixture<NestedReplyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NestedReplyComponent]
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
