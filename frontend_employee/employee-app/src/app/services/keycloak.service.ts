import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface KeycloakConfig {
  url: string;
  realm: string;
  clientId: string;
}

interface KeycloakTokenParsed {
  preferred_username?: string;
  email?: string;
  given_name?: string;
  family_name?: string;
  [key: string]: unknown;
}

interface KeycloakProfile {
  username?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private keycloakAuth: Keycloak.KeycloakInstance;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private userRolesSubject = new BehaviorSubject<string[]>([]);
  private userProfileSubject = new BehaviorSubject<KeycloakProfile | null>(null);
  private initialized = false;

  constructor() {
    const config: KeycloakConfig = {
      url: 'http://localhost:8180',
      realm: 'Employee',
      clientId: 'frontend_employee'
    };
    this.keycloakAuth = new Keycloak(config);
  }

  init(): Promise<boolean> {
    if (this.initialized) {
      return Promise.resolve(this.isAuthenticatedSubject.value);
    }

    return new Promise<boolean>((resolve, reject) => {
      this.keycloakAuth.init({
        onLoad: 'login-required',
        redirectUri: `${window.location.origin}${window.location.pathname}`,
        checkLoginIframe: false
      }).then(authenticated => {
        this.initialized = true;
        this.updateAuthState();
        if (authenticated) {
          this.loadUserProfile().catch(error => {
            console.error('Profile loading failed:', error);
          });
        }
        resolve(authenticated);
      }).catch(error => {
        console.error('Keycloak initialization failed:', error);
        reject(error);
      });
    });
  }

  private updateAuthState(): void {
    const authenticated = !!this.keycloakAuth.authenticated;
    this.isAuthenticatedSubject.next(authenticated);
    
    const roles = this.keycloakAuth.realmAccess?.roles || [];
    this.userRolesSubject.next(roles);
    
    if (!authenticated) {
      this.userProfileSubject.next(null);
    }
  }

  login(): Promise<void> {
    return this.keycloakAuth.login()
      .then(() => this.updateAuthState())
      .catch(error => {
        console.error('Login failed:', error);
        throw error;
      });
  }

  logout(): Promise<void> {
    return this.keycloakAuth.logout({
      redirectUri: window.location.origin
    }).then(() => {
      this.updateAuthState();
    }).catch(error => {
      console.error('Logout failed:', error);
      throw error;
    });
  }

  isLoggedIn(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  getRoles(): Observable<string[]> {
    return this.userRolesSubject.asObservable();
  }

  hasRole(role: string): Observable<boolean> {
    return this.getRoles().pipe(
      map(roles => roles.includes(role))
    );
  }

  loadUserProfile(): Promise<KeycloakProfile> {
    return new Promise((resolve, reject) => {
      this.keycloakAuth.loadUserProfile()
        .then(profile => {
          const formattedProfile: KeycloakProfile = {
            username: profile.username || this.getUsername(),
            email: profile.email,
            firstName: profile.firstName,
            lastName: profile.lastName,
            ...profile
          };
          this.userProfileSubject.next(formattedProfile);
          resolve(formattedProfile);
        })
        .catch(error => {
          console.error('Failed to load user profile:', error);
          reject(error);
        });
    });
  }

  getUserProfile(): Observable<KeycloakProfile | null> {
    return this.userProfileSubject.asObservable();
  }

  getTokenParsed(): KeycloakTokenParsed | undefined {
    return this.keycloakAuth.tokenParsed;
  }

  getUsername(): string {
    return this.getTokenParsed()?.preferred_username || '';
  }

  getToken(): string {
    return this.keycloakAuth.token || '';
  }

  refreshToken(): Promise<boolean> {
    return this.keycloakAuth.updateToken(30)
      .then(refreshed => {
        if (refreshed) {
          this.updateAuthState();
        }
        return refreshed;
      })
      .catch(error => {
        console.error('Token refresh failed:', error);
        throw error;
      });
  }

  isInitialized(): boolean {
    return this.initialized;
  }

  getKeycloakInstance(): Keycloak.KeycloakInstance {
    return this.keycloakAuth;
  }
}