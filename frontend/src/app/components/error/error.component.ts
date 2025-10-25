import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ErrorService } from '../../service/error.service';

@Component({
  selector: 'app-error',
  standalone: true,
  imports: [CommonModule],
  templateUrl: 'error.component.html',})
    

export class ErrorComponent {
  private router = inject(Router);
  private errorService = inject(ErrorService);
  error = signal(this.errorService.getError());

  goBack() {
    this.router.navigate(['/login']);
  }
}