import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { EmployeeService, Employee, PaginatedEmployees } from '../../services/employee.service';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator'; 

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatSortModule, MatButtonModule],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css']
})
export class EmployeeListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'email', 'immatriculation']; // Added
  dataSource = new MatTableDataSource<Employee>([]);
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees(this.currentPage, this.pageSize);
  }

  loadEmployees(page: number, size: number): void {
    this.employeeService.getEmployees(page, size).subscribe((data: PaginatedEmployees) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.currentPage = data.number;
    });
  }

  onPaginateChange(event: PageEvent): void {  
    this.loadEmployees(event.pageIndex, event.pageSize);
  }
}