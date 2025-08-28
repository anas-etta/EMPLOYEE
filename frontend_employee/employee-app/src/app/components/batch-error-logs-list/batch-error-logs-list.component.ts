import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon'; // <-- ADD THIS LINE
import { BatchErrorLogService, BatchErrorLog, PaginatedBatchErrorLogs } from '../../services/batch-error-logs.service';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-batch-error-logs-list',
  standalone: true,
  imports: [
    CommonModule, 
    MatTableModule, 
    MatPaginatorModule, 
    MatIconModule 
  ],
  templateUrl: './batch-error-logs-list.component.html',
  styleUrls: ['./batch-error-logs-list.component.css']
})
export class BatchErrorLogListComponent implements OnInit {
 
  displayedColumns: string[] = ['id', 'fileName', 'ligne', 'errorMessage'];
  dataSource = new MatTableDataSource<BatchErrorLog>([]);
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;
  filterFileName: string | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private batchErrorLogService: BatchErrorLogService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    
    this.route.queryParams.subscribe(params => {
      this.filterFileName = params['fileName'] || null;
      if (this.filterFileName) {
        this.loadBatchErrorLogsByFileName(this.filterFileName);
      } else {
        this.loadBatchErrorLogs(this.currentPage, this.pageSize);
      }
    });
  }

  loadBatchErrorLogs(page: number, size: number): void {
    this.batchErrorLogService.getBatchErrorLogs(page, size).subscribe((data: PaginatedBatchErrorLogs) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.currentPage = data.number;
    });
  }

  loadBatchErrorLogsByFileName(fileName: string): void {
    this.batchErrorLogService.getBatchErrorLogsByFileName(fileName).subscribe(logs => {
      this.dataSource.data = logs;
      this.totalElements = logs.length;
    });
  }

  onPaginateChange(event: PageEvent): void {
    if (this.filterFileName) {
      this.loadBatchErrorLogsByFileName(this.filterFileName);
    } else {
      this.loadBatchErrorLogs(event.pageIndex, event.pageSize);
    }
  }

  retour(): void {
    this.router.navigate(['/batch-traitement']);
  }
}