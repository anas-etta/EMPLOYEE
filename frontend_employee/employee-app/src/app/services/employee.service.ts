import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KeycloakService } from './keycloak.service';

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  immatriculation: string;
}

export interface PaginatedEmployees {
  content: Employee[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private baseUrl = 'http://localhost:8080/api/employees';

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.keycloakService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getEmployees(page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'id,desc'); 
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}`, { params, headers });
  }

  
  searchEmployeesByMultipleFields(
    firstName: string,
    lastName: string,
    email: string,
    immatriculation: string,
    page: number,
    size: number
  ): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'id,desc'); 
    if (firstName) params = params.set('firstName', firstName);
    if (lastName) params = params.set('lastName', lastName);
    if (email) params = params.set('email', email);
    if (immatriculation) params = params.set('immatriculation', immatriculation);
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}/search`, { params, headers });
  }

  searchEmployeesByField(field: string, value: string, page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams()
      .set('field', field)
      .set('value', value)
      .set('page', page)
      .set('size', size)
      .set('sort', 'id,desc'); 
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}/search-by-field`, { params, headers });
  }

  searchEmployees(query: string, page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams()
      .set('query', query)
      .set('page', page)
      .set('size', size)
      .set('sort', 'id,desc'); 
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}/search`, { params, headers });
  }

  deleteEmployee(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers });
  }

  createEmployee(employee: Employee): Observable<Employee> {
    const headers = this.getAuthHeaders();
    return this.http.post<Employee>(this.baseUrl, employee, { headers });
  }

  updateEmployee(id: number, employee: Employee): Observable<Employee> {
    const headers = this.getAuthHeaders();
    return this.http.put<Employee>(`${this.baseUrl}/${id}`, employee, { headers });
  }

  checkEmailExists(email: string, id?: number): Observable<boolean> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams().set('email', email);
    if (id !== undefined) params = params.set('id', id.toString());
    return this.http.get<boolean>(`${this.baseUrl}/check-email`, { params, headers });
  }

  
  checkImmatriculationExists(immatriculation: string, id?: number): Observable<boolean> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams().set('immatriculation', immatriculation);
    if (id !== undefined) params = params.set('id', id.toString());
    return this.http.get<boolean>(`${this.baseUrl}/check-immatriculation`, { params, headers });
  }
}