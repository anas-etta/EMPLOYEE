import { Component, OnInit, OnDestroy } from '@angular/core';
import { KeycloakService } from './services/keycloak.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { EmployeeCrudComponent } from './components/employee-crud/employee-crud.component';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    RouterOutlet,
    NavbarComponent,
    EmployeeCrudComponent 
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'employee-app';
  isLoading = true;
  private refreshSub?: Subscription;

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  ngOnInit() {
    this.keycloakService.init().then(() => {
      console.log('Keycloak initialized');
      this.isLoading = false;

      // Start periodic token refresh every 60 seconds
      this.refreshSub = interval(60000).subscribe(() => {
        this.keycloakService.refreshToken().catch(err => {
          console.error('Token refresh failed:', err);
          this.keycloakService.logout();
          this.router.navigate(['/login']);
        });
      });

    }).catch(() => {
      console.error('Keycloak initialization failed');
      this.isLoading = false; 
      this.router.navigate(['/login']);
    });
  }

  ngOnDestroy() {
    this.refreshSub?.unsubscribe();
  }
}