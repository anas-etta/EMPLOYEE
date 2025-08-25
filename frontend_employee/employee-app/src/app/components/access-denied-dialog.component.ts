import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-access-denied-dialog',
  template: `
    <div class="dialog-container">
      <h1 mat-dialog-title class="title">Accès Refusé</h1>
      
      <div mat-dialog-content class="content">
        <p>Vous n'avez pas l'autorisation d'accéder à cette page.</p>
      </div>
      
      <div mat-dialog-actions class="actions">
        <button mat-button (click)="close()">Fermer</button>
      </div>
    </div>
  `,
  styles: [`
    .dialog-container {
      padding: 24px;
      max-width: 350px;
      text-align: center;
    }
    .title {
      color: #d32f2f;
      margin: 0 0 16px 0;
      font-size: 1.5rem;
      font-weight: 500;
    }
    .content {
      color: #616161;
      margin-bottom: 24px;
      line-height: 1.5;
    }
    .actions {
      display: flex;
      justify-content: center;
      padding: 0;
    }
    button {
      padding: 6px 16px;
      border-radius: 4px;
      background-color: #d32f2f;
      color: white;
    }
    button:hover {
      background-color: #b71c1c;
    }
  `]
})
export class AccessDeniedDialogComponent {
  constructor(private dialogRef: MatDialogRef<AccessDeniedDialogComponent>) {}

  close() {
    this.dialogRef.close();
  }
}