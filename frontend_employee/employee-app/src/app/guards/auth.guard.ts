import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { KeycloakService } from '../services/keycloak.service';
import { AccessDeniedDialogComponent } from '../components/access-denied-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private keycloakService: KeycloakService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  canActivate(
    next: ActivatedRouteSnapshot
  ): Observable<boolean> {
    return this.keycloakService.isLoggedIn().pipe(
      switchMap((loggedIn: boolean) => {
        if (!loggedIn) {
          this.router.navigate(['/login']);
          return of(false);
        }

        return this.keycloakService.getRoles().pipe(
          map((roles: string[]) => {
            const requiredRoles = next.data['roles'] as string[];
            if (requiredRoles && !requiredRoles.some(role => roles.includes(role))) {
              this.dialog.open(AccessDeniedDialogComponent);
              this.router.navigate(['/access-denied']);
              return false;
            }
            return true;
          }),
          catchError(() => {
            this.router.navigate(['/access-denied']);
            return of(false);
          })
        );
      })
    );
  }
}