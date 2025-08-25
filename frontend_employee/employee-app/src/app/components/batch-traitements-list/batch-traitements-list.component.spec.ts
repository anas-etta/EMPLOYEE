import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchTraitementsListComponent } from './batch-traitements-list.component';

describe('BatchTraitementsListComponent', () => {
  let component: BatchTraitementsListComponent;
  let fixture: ComponentFixture<BatchTraitementsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BatchTraitementsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BatchTraitementsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});