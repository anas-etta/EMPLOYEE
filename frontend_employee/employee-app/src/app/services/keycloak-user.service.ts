import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { KeycloakService } from './keycloak.service';

export interface KeycloakUser {
  id?: string;
  username: string;
  role: string;
  password?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class KeycloakUserService {
  private baseUrl = 'http://localhost:8080/api/admin/keycloak-users';

  constructor(private http: HttpClient, private keycloakService: KeycloakService) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.keycloakService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getUsers(): Observable<KeycloakUser[]> {
    return this.http.get<KeycloakUser[]>(this.baseUrl, { headers: this.getAuthHeaders() });
  }

  createUser(user: KeycloakUser): Observable<KeycloakUser> {
    return this.http.post<KeycloakUser>(this.baseUrl, user, { headers: this.getAuthHeaders() });
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  // NEW: Check if email exists in Keycloak
  checkEmailExists(email: string): Observable<boolean> {
    return this.http.get<{ exists: boolean }>(
      `${this.baseUrl}/check-email`,
      { params: { email }, headers: this.getAuthHeaders() }
    ).pipe(map(res => res.exists));
  }
}