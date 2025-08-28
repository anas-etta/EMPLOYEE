import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KeycloakService } from './keycloak.service';
import { BatchErrorLog } from './batch-error-logs.service';

export interface BatchTraitement {
  id: number;
  nomFichier: string;
  nbrLigne: number;
  nbrLigneValide: number;
  nbrLigneInvalide: number;
  startTime: string; 
  stopTime: string;  
}

export interface PaginatedBatchTraitements {
  content: BatchTraitement[];
  totalElements: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class BatchTraitementService {
  private baseUrl = 'http://localhost:8080/api/batch-traitements';
  private errorLogsUrl = 'http://localhost:8080/api/batch-error-logs';

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.keycloakService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getBatchTraitements(page: number, size: number): Observable<PaginatedBatchTraitements> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    const headers = this.getAuthHeaders();
    return this.http.get<PaginatedBatchTraitements>(this.baseUrl, { params, headers });
  }

  getErrorLogsByFileName(fileName: string): Observable<BatchErrorLog[]> {
    const headers = this.getAuthHeaders();
    
    return this.http.get<BatchErrorLog[]>(
      `${this.errorLogsUrl}/by-filename/${encodeURIComponent(fileName)}`,
      { headers }
    );
  }
}