import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ExperienceResponse } from '../dto/experience-response';
import { GamificationService } from '../gamification.service';

@Component({
  selector: 'app-hero',
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css']
})
export class HeroComponent implements OnInit {
  experience: number = -1;
  error: boolean = false;
  errorMsg: String = '';

  constructor(private gamification: GamificationService) { }

  ngOnInit(): void {
    this.gamification.getExperience(1).subscribe({
      next: (response: ExperienceResponse) => this.onSuccess(response.experience),
      error: (error: HttpErrorResponse) => this.onError(error)
    });
  }

  onSuccess(experience: number): void {
    this.experience = experience;
  }

  onError(error: HttpErrorResponse): void {
    this.error = true;
    this.errorMsg = error.error.message;
  }
  

}
