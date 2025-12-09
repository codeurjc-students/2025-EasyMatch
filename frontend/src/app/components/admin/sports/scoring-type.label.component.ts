import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'scoringTypeLabel',
  standalone: true
})
export class ScoringTypeLabelPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'SCORE': return 'Puntos';
      case 'SETS': return 'Sets';
      default: return value ?? '';
    }
  }
}