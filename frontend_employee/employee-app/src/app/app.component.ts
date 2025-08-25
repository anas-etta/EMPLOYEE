import { Component, OnInit } from '@angular/core';
import { KeycloakService } from './services/keycloak.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { EmployeeCrudComponent } from './components/employee-crud/employee-crud.component';

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
export class AppComponent implements OnInit {
  title = 'employee-app';
  isLoading = true;

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  ngOnInit() {
    this.keycloakService.init().then(() => {
      console.log('Keycloak initialized');
      this.isLoading = false;
    }).catch(() => {
      console.error('Keycloak initialization failed');
      this.isLoading = false; 
      this.router.navigate(['/login']);
    });
  }
}