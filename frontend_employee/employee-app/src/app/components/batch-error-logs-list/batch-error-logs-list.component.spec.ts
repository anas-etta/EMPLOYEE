import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchErrorLogListComponent } from './batch-error-logs-list.component';

describe('BatchErrorLogListComponent', () => {
  let component: BatchErrorLogListComponent;
  let fixture: ComponentFixture<BatchErrorLogListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BatchErrorLogListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BatchErrorLogListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});