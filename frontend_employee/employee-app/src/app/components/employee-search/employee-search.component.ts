import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { EmployeeService, Employee, PaginatedEmployees } from '../../services/employee.service';

@Component({
  selector: 'app-employee-search',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatInputModule, MatButtonModule, FormsModule, MatSelectModule
  ],
  templateUrl: './employee-search.component.html',
  styleUrls: ['./employee-search.component.css']
})
export class EmployeeSearchComponent implements OnInit {
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'email', 'immatriculation'];
  dataSource = new MatTableDataSource<Employee>([]);
  totalElements = 0;

  search = {
    firstName: '',
    lastName: '',
    email: '',
    immatriculation: ''
  };

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadData(0, 10);
  }

  loadData(page: number, size: number): void {
    if (this.search.firstName || this.search.lastName || this.search.email || this.search.immatriculation) {
      this.employeeService.searchEmployeesByMultipleFields(
        this.search.firstName,
        this.search.lastName,
        this.search.email,
        this.search.immatriculation,
        page,
        size
      ).subscribe((data: PaginatedEmployees) => {
        this.dataSource.data = data.content;
        this.totalElements = data.totalElements;
        this.paginator.length = data.totalElements;
      });
    } else {
      this.employeeService.getEmployees(page, size).subscribe((data: PaginatedEmployees) => {
        this.dataSource.data = data.content;
        this.totalElements = data.totalElements;
        this.paginator.length = data.totalElements;
      });
    }
  }

  searchEmployees(): void {
    this.paginator.firstPage();
    this.loadData(0, this.paginator.pageSize || 10);
  }

  onPaginateChange(event: PageEvent): void {
    this.loadData(event.pageIndex, event.pageSize);
  }
}