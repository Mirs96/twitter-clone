import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralTweetsComponent } from './general-tweets.component';

describe('GeneralTweetsComponent', () => {
  let component: GeneralTweetsComponent;
  let fixture: ComponentFixture<GeneralTweetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralTweetsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralTweetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
