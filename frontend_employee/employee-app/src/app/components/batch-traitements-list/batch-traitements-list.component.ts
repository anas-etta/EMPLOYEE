import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { BatchTraitementService, BatchTraitement, PaginatedBatchTraitements } from '../../services/batch-traitement.service';
import { MatTableDataSource } from '@angular/material/table';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-batch-traitements-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, RouterModule],
  templateUrl: './batch-traitements-list.component.html',
  styleUrls: ['./batch-traitements-list.component.css']
})
export class BatchTraitementsListComponent implements OnInit {
  displayedColumns: string[] = [
    'id', 'nomFichier', 'nbrLigne', 'nbrLigneValide', 'nbrLigneInvalide', 'startTime', 'stopTime'
  ];
  dataSource = new MatTableDataSource<BatchTraitement>([]);
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private batchTraitementService: BatchTraitementService) {}

  ngOnInit(): void {
    this.loadBatchTraitements(this.currentPage, this.pageSize);
  }

  loadBatchTraitements(page: number, size: number): void {
    this.batchTraitementService.getBatchTraitements(page, size).subscribe((data: PaginatedBatchTraitements) => {
      const sorted = [...data.content].sort((a, b) => {
        const dateA = this.parseDate(a.startTime)?.getTime() || 0;
        const dateB = this.parseDate(b.startTime)?.getTime() || 0;
        return dateB - dateA;
      });
      this.dataSource.data = sorted;
      this.totalElements = data.totalElements;
      this.currentPage = data.number;
    });
  }

  onPaginateChange(event: PageEvent): void {
    this.loadBatchTraitements(event.pageIndex, event.pageSize);
  }

  parseDate(dateString: string): Date | null {
    if (!dateString) return null;
    const [datePart, timePart] = dateString.split(' ');
    const [day, month, year] = datePart.split('/').map(Number);
    let hours = 0, minutes = 0, seconds = 0;
    if (timePart) {
      const [h, m, s] = timePart.split(':').map(Number);
      hours = h || 0;
      minutes = m || 0;
      seconds = s || 0;
    }
    if (year && month && day) {
      return new Date(year, month - 1, day, hours, minutes, seconds);
    }
    return null;
  }
}