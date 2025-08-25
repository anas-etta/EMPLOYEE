import { Routes } from '@angular/router';
import { EmployeeListComponent } from './components/employee-list/employee-list.component'; 
import { EmployeeSearchComponent } from './components/employee-search/employee-search.component'; 
import { EmployeeCrudComponent } from './components/employee-crud/employee-crud.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { AuthGuard } from './guards/auth.guard';  
import { BatchErrorLogListComponent } from './components/batch-error-logs-list/batch-error-logs-list.component'; 
import { BatchTraitementsListComponent } from './components/batch-traitements-list/batch-traitements-list.component';

export const routes: Routes = [
  { 
    path: '', 
    redirectTo: '/employees', 
    pathMatch: 'full' 
  },
  { 
    path: 'employees', 
    component: EmployeeListComponent,
    title: 'Liste des employés'
  },
  { 
    path: 'search', 
    component: EmployeeSearchComponent,
    title: 'Recherche d\'employés'
  },
  {
    path: 'employees-crud',
    component: EmployeeCrudComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] },
    title: 'Gestion des employés'
  },
  {
    path: 'user-management',
    component: UserManagementComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] },
    title: 'Gérer les utilisateurs'
  },
  {
    path: 'batch-error-logs',
    component: BatchErrorLogListComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] },
    title: 'Batch Error Logs'
  },
  {
    path: 'batch-traitement',
    component: BatchTraitementsListComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] },
    title: 'Batch Traitement'
  }
];