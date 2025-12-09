import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss']
})
export class ErrorPageComponent {

  @Input() code: number = 500;
  @Input() title: string = 'Error';
  @Input() message: string = 'Ha ocurrido un error inesperado.';

  constructor(private router: Router,private route: ActivatedRoute) {}

  ngOnInit() {
    const params = this.route.snapshot.queryParams;

    if (params['code'])   this.code = +params['code'];
    if (params['title'])  this.title = params['title'];
    if (params['message']) this.message = params['message'];
  }
  goHome() {
    this.router.navigate(['/']);
  }

  goLogin() {
    this.router.navigate(['/login']);
  }
}
