import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { KeycloakService } from '../../services/keycloak.service';
import { KeycloakProfile } from 'keycloak-js';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  navItems = [
    
    { path: '/search', label: 'Rechercher un employé' },
    { 
      path: '/employees-crud', 
      label: 'Gérer un employé', 
      requiredRole: 'ROLE_ADMIN'
    },
    {
      path: '/user-management',
      label: 'Utilisateurs',
      requiredRole: 'ROLE_ADMIN'
    },
    
    {
      path: '/batch-traitement',
      label: 'Batch traitement',
      requiredRole: 'ROLE_ADMIN'
    }
  ];

  isAdmin = false;
  userProfile: KeycloakProfile | null = null;

  constructor(public keycloakService: KeycloakService) {}

  async ngOnInit() {
    try {
      this.isAdmin = await firstValueFrom(this.keycloakService.hasRole('ROLE_ADMIN'));
      const isLoggedIn = await firstValueFrom(this.keycloakService.isLoggedIn());
      if (isLoggedIn) {
        this.userProfile = await this.keycloakService.loadUserProfile();
      }
    } catch (error) {
      console.error('Error initializing navbar:', error);
    }
  }

  login() {
    this.keycloakService.login().catch(console.error);
  }

  logout() {
    this.keycloakService.logout().catch(console.error);
  }
}