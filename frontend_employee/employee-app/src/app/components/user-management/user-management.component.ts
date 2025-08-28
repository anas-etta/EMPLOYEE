import { Component, OnInit } from '@angular/core';
import { KeycloakUserService, KeycloakUser } from '../../services/keycloak-user.service';
import { KeycloakService } from '../../services/keycloak.service';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject, of } from 'rxjs';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css'
})
export class UserManagementComponent implements OnInit {
  users: KeycloakUser[] = [];
  newUser: KeycloakUser = {
    username: '',
    role: 'ROLE_USER',
    password: '',
    email: '',
    firstName: '',
    lastName: ''
  };
  editingUser: KeycloakUser | null = null;
  message = '';
  loading = false;

  roles = ['ROLE_USER', 'ROLE_ADMIN'];

  emailExists = false;
  private emailCheck$ = new Subject<string>();
  currentUsername: string = '';

  constructor(
    private userService: KeycloakUserService,
    private keycloakService: KeycloakService
  ) {}

  ngOnInit() {
    this.currentUsername = this.keycloakService.getUsername(); 
    this.loadUsers();

    this.emailCheck$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(email => {
        if (!email || !this.isEmailFormatValid(email)) return of(false);
        return this.userService.checkEmailExists(email);
      })
    ).subscribe(exists => this.emailExists = exists);
  }

  loadUsers() {
    this.loading = true;
    this.userService.getUsers().subscribe({
      next: users => {
        
        this.users = users.filter(u => u.username !== this.currentUsername);
        this.loading = false;
      },
      error: () => {
        this.message = 'Erreur lors du chargement des utilisateurs';
        this.loading = false;
      }
    });
  }

  createUser(userForm: NgForm) {
    if (!this.newUser.username || !this.newUser.role || !this.newUser.password
      || !this.newUser.email || !this.newUser.firstName || !this.newUser.lastName) {
      this.message = 'Tous les champs sont obligatoires';
      return;
    }
    if (this.newUser.username.length < 3 || this.newUser.username.length > 255) {
      this.message = 'Le nom d\'utilisateur doit contenir entre 3 et 255 caractères.';
      return;
    }
    if (this.emailExists) {
      this.message = 'Cet email est déjà utilisé';
      return;
    }
    this.userService.createUser(this.newUser).subscribe({
      next: () => {
        this.message = 'Utilisateur créé avec succès';
        userForm.resetForm({ role: 'ROLE_USER' });
        this.loadUsers();
      },
      error: () => this.message = 'Erreur lors de la création de l\'utilisateur'
    });
  }

  deleteUser(user: KeycloakUser) {
    if (user.id) {
      if (confirm(`Supprimer l'utilisateur ${user.username} ?`)) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.message = 'Utilisateur supprimé';
            this.loadUsers();
          },
          error: () => this.message = 'Erreur lors de la suppression'
        });
      }
    }
  }

  onEmailInput(email: string) {
    this.emailCheck$.next(email ?? '');
  }

  isEmailFormatValid(email: string): boolean {
    return /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/.test(email);
  }

  
  editUser(user: KeycloakUser) {
    this.editingUser = { ...user }; 
  }

  cancelEdit() {
    this.editingUser = null;
  }

  updateUser() {
    if (!this.editingUser) return;
    const { username, role, email, firstName, lastName, id } = this.editingUser;
    if (!username || !role || !email || !firstName || !lastName) {
      this.message = 'Tous les champs sont obligatoires';
      return;
    }
    if (username.length < 3 || username.length > 255) {
      this.message = 'Le nom d\'utilisateur doit contenir entre 3 et 255 caractères.';
      return;
    }
    this.userService.updateUser(this.editingUser).subscribe({
      next: () => {
        this.message = 'Utilisateur mis à jour avec succès';
        this.editingUser = null;
        this.loadUsers();
      },
      error: () => this.message = 'Erreur lors de la mise à jour'
    });
  }
}