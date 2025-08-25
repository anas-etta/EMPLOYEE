import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule, FormGroup, FormControl, Validators, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { EmployeeService, Employee, PaginatedEmployees } from '../../services/employee.service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

interface ColumnDefinition {
  id: string;
  header: string;
  sortable: string | false;  
}

@Component({
  selector: 'app-employee-crud',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatInputModule, MatButtonModule, ReactiveFormsModule
  ],
  templateUrl: './employee-crud.component.html',
  styleUrls: ['./employee-crud.component.css']
})
export class EmployeeCrudComponent implements OnInit {
  columnDefinitions: ColumnDefinition[] = [
    { id: 'id', header: 'ID', sortable: 'id' },
    { id: 'firstName', header: 'Prénom', sortable: 'firstName' },
    { id: 'lastName', header: 'Nom', sortable: 'lastName' },
    { id: 'email', header: 'Email', sortable: 'email' },
    { id: 'immatriculation', header: 'Immatriculation', sortable: 'immatriculation' },
    { id: 'actions', header: 'Actions', sortable: false }
  ];
  displayedColumns = this.columnDefinitions.map(col => col.id);
  dataSource = new MatTableDataSource<Employee>([]);
  totalElements = 0;
  selectedEmployee: Employee | null = null;
  isEditing = false;

  employeeForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl(
      '',
      [
        Validators.required,
        Validators.pattern('^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$')
      ],
      [] // set async validator dynamically
    ),
    immatriculation: new FormControl(
      '',
      [
        Validators.required,
        Validators.pattern('^RH\\d{4}$')
      ],
      [] // set async validator dynamically
    )
  });

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    // Set async validators with DI service and current editing id
    this.setAllAsyncValidators();
    this.loadData(0, 10);
  }

  private setAllAsyncValidators(): void {
    // Use a closure to capture the current editing id in validators
    this.employeeForm.get('email')?.setAsyncValidators(this.emailAsyncValidator());
    this.employeeForm.get('immatriculation')?.setAsyncValidators(this.immatriculationAsyncValidator());
  }

  private loadData(page: number, size: number): void {
    this.employeeService.getEmployees(page, size).subscribe((data: PaginatedEmployees) => {
      this.updateTableData(data);
    });
  }

  private updateTableData(data: PaginatedEmployees): void {
    this.dataSource.data = data.content;
    this.totalElements = data.totalElements;
    if (this.paginator) {
      this.paginator.length = data.totalElements;
    }
  }

  private resetToFirstPage(): void {
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
  }

  onPaginateChange(event: PageEvent): void {
    this.loadData(event.pageIndex, event.pageSize);
  }

  deleteEmployee(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet employé ?')) {
      this.employeeService.deleteEmployee(id).subscribe(() => {
        this.resetAndReload();
      });
    }
  }

  editEmployee(employee: Employee): void {
    this.selectedEmployee = employee;
    this.isEditing = true;
    this.employeeForm.patchValue({
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      immatriculation: employee.immatriculation
    });
    // Reset async validators to capture new editing id
    this.setAllAsyncValidators();
    // Reset state
    this.employeeForm.get('email')?.markAsPristine();
    this.employeeForm.get('email')?.markAsUntouched();
    this.employeeForm.get('immatriculation')?.markAsPristine();
    this.employeeForm.get('immatriculation')?.markAsUntouched();
  }

  addNewEmployee(): void {
    this.selectedEmployee = null;
    this.isEditing = false;
    this.employeeForm.reset();
    this.setAllAsyncValidators();
  }

  saveEmployee(): void {
    if (this.employeeForm.valid) {
      const employeeData = this.employeeForm.value as Employee;
      const saveOperation = this.isEditing && this.selectedEmployee
        ? this.employeeService.updateEmployee(this.selectedEmployee.id, employeeData)
        : this.employeeService.createEmployee(employeeData);

      saveOperation.subscribe(() => {
        this.resetAndReload();
        if (this.isEditing) {
          this.isEditing = false;
        }
        this.employeeForm.reset();
        this.setAllAsyncValidators();
      });
    }
  }

  private resetAndReload(): void {
    this.resetToFirstPage();
    this.loadData(0, this.paginator?.pageSize || 10);
  }

  // --------- Email async validator (checks with backend) -----------
  emailAsyncValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }
      const id = this.isEditing && this.selectedEmployee ? this.selectedEmployee.id : undefined;
      return this.employeeService.checkEmailExists(control.value, id).pipe(
        map((exists: boolean) => (exists ? { emailTaken: true } : null))
      );
    };
  }

  // --------- Immatriculation async validator (checks with backend) -----------
  immatriculationAsyncValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }
      const id = this.isEditing && this.selectedEmployee ? this.selectedEmployee.id : undefined;
      return this.employeeService.checkImmatriculationExists(control.value, id).pipe(
        map((exists: boolean) => (exists ? { immatriculationTaken: true } : null))
      );
    };
  }
}