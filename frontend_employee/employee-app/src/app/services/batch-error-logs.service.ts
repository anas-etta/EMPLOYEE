import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KeycloakService } from './keycloak.service';  // Import!

export interface BatchErrorLog {
  id: number;
  errorMessage: string;
  fileName: string;
  ligne: number;
}

export interface PaginatedBatchErrorLogs {
  content: BatchErrorLog[];
  totalElements: number;
  number: number; // current page (0-based)
}

@Injectable({
  providedIn: 'root'
})
export class BatchErrorLogService {
  private baseUrl = 'http://localhost:8080/api/batch-error-logs';

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService   // Inject!
  ) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.keycloakService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getBatchErrorLogs(page: number, size: number): Observable<PaginatedBatchErrorLogs> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    const headers = this.getAuthHeaders();
    return this.http.get<PaginatedBatchErrorLogs>(this.baseUrl, { params, headers });
  }

  getBatchErrorLogsByFileName(fileName: string): Observable<BatchErrorLog[]> {
    const headers = this.getAuthHeaders();
    // Ensure backend supports this endpoint
    return this.http.get<BatchErrorLog[]>(
      `${this.baseUrl}/by-filename/${encodeURIComponent(fileName)}`,
      { headers }
    );
  }
}