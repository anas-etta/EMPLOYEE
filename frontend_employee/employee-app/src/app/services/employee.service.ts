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
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}?page=${page}&size=${size}`, { headers });
  }

  // New: Search by field
  searchEmployeesByField(field: string, value: string, page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams()
      .set('field', field)
      .set('value', value)
      .set('page', page)
      .set('size', size);
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}/search-by-field`, { params, headers });
  }

  // Old: Search generic (still here if you want to keep it)
  searchEmployees(query: string, page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}/search?query=${query}&page=${page}&size=${size}`, { headers });
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

  // ---------- Email existence check (accepts optional id) ----------
  checkEmailExists(email: string, id?: number): Observable<boolean> {
    const headers = this.getAuthHeaders();
    const params: any = { email };
    if (id !== undefined) params.id = id;
    return this.http.get<boolean>(`${this.baseUrl}/check-email`, { params, headers });
  }

  // ---------- Immatriculation existence check (accepts optional id) ----------
  checkImmatriculationExists(immatriculation: string, id?: number): Observable<boolean> {
    const headers = this.getAuthHeaders();
    const params: any = { immatriculation };
    if (id !== undefined) params.id = id;
    return this.http.get<boolean>(`${this.baseUrl}/check-immatriculation`, { params, headers });
  }
}