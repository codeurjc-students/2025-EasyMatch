import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClubService } from '../../../service/club.service';
import { Club } from '../../../models/club.model';

@Component({
  selector: 'app-admin-club-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatDatepickerModule
],
  templateUrl: './admin-club-create.component.html',
  styleUrls: ['.././admin-entity-create.component.scss']
})
export class AdminClubCreateComponent implements OnInit {

  private fb = inject(FormBuilder);
  private clubService = inject(ClubService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  form!: FormGroup;
  editingId: number | null = null;
  photoFile: File | null = null;
  photoPreview: string | null = null;
  
  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      email: ['', [Validators.required, Validators.email]],
      city: ['', Validators.required],
      web: ['', Validators.required],
      address: ['', Validators.required],
      openingTime: ['', Validators.required],
      closingTime: ['', Validators.required],
      minPrice: ['', [Validators.required, Validators.min(0)]],
      maxPrice: ['', [Validators.required, Validators.min(0)]],
      
    });
    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.editingId = +params['id'];
        this.loadClub(+params['id']);
      }
    });
  };

  
  loadClub(id: number) {
    this.clubService.getClub(id).subscribe({
      next: (c: Club) => {
        this.form.patchValue({
          name: c.name,
          address: c.address,
          city: c.city,
          email: c.email,
          web : c.web,
          phone: c.phone,
          openingTime: c.schedule.openingTime,
          closingTime: c.schedule.closingTime,
          minPrice: c.priceRange.minPrice,
          maxPrice: c.priceRange.maxPrice,
        });
        this.clubService.getClubImage(id).subscribe({
          next: (blob: Blob) => {
            const reader = new FileReader();
            reader.onload = () => this.photoPreview = reader.result as string;
            reader.readAsDataURL(blob);
          },
          error: () => {
            this.photoPreview = null; 
          }
        });
      }
      
    });
    
  };

   save() {
    if (this.form.invalid) return;
    const f = this.form.value;
    const payload = {
      name: f.name,
      phone: f.phone,
      email: f.email,
      city: f.city,
      address: f.address,
      web: f.web,

      schedule: {
        openingTime: f.openingTime,
        closingTime: f.closingTime
      },

      priceRange: {
        minPrice: f.minPrice,
        maxPrice: f.maxPrice,
        unit: "€ / hora"  
      }
    };

    if (this.editingId) {
      this.clubService.updateClub(this.editingId, payload).subscribe({
        next: (club : Club) => {
          if (this.photoFile) {
            this.clubService.replaceClubImage(club.id, this.photoFile).subscribe({
              next: () => {
                this.snackBar.open('✅ Club editado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
                this.reloadAfterSave();
              },
              error: (err) => {
                console.error('Error al subir foto:', err);
                this.snackBar.open('❌ Club guardado pero error subiendo la foto', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
                this.reloadAfterSave();
              }
            });
          } else {
            this.snackBar.open('✅ Club editado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
            this.reloadAfterSave();
          }
        },
        error: (err) => {
          console.error('Error al actualizar club:', err);
          this.snackBar.open('❌ Error al editar club', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
        }
      });

    } else {
      this.clubService.createClub(payload).subscribe({
        next: (created: any) => {
          const newId = created?.id;
          if (this.photoFile && newId) {
            this.clubService.replaceClubImage(newId, this.photoFile).subscribe({
              next: () => {
                this.snackBar.open('✅ Club creado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
                window.location.href = '/admin/clubs';
              },
              error: (err) => {
                console.error('Error al subir foto tras crear club:', err);
                this.snackBar.open('❌ Club creado, pero error subiendo foto', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
                window.location.href = '/admin/clubs';
              }
            });
          } else {
            this.snackBar.open('✅ Club creado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
            window.location.href = '/admin/clubs';
          }
        },
        error: (err) => {
          console.error('Error al crear club:', err);
          this.snackBar.open('❌ Error al crear club', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
        }
      });
    }
  }

  private reloadAfterSave() {
    if (this.editingId) {
      this.clubService.getClubImage(this.editingId).subscribe({
        next: (blob: Blob) => {
          const reader = new FileReader();
          reader.onload = () => this.photoPreview = reader.result as string;
          reader.readAsDataURL(blob);
          setTimeout(() => window.location.href = '/admin/clubs', 800);
        },
        error: () => window.location.href = '/admin/clubs'
      });
    } else {
      window.location.href = '/admin/clubs';
    }
  }

  onPhotoSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.photoFile = file;

    const reader = new FileReader();
    reader.onload = () => this.photoPreview = reader.result as string;
    reader.readAsDataURL(file);
  }

  cancel() {
    window.location.href = '/admin/clubs';
  }
}
