import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { InputRowComponent } from 'app/common/input-row/input-row.component';
import { SessionService } from 'app/session/session.service';
import { SessionDTO } from 'app/session/session.model';
import { ErrorHandler } from 'app/common/error-handler.injectable';


@Component({
  selector: 'app-session-add',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, InputRowComponent],
  templateUrl: './session-add.component.html'
})
export class SessionAddComponent implements OnInit {

  sessionService = inject(SessionService);
  router = inject(Router);
  errorHandler = inject(ErrorHandler);

  personValues?: Map<number,string>;

  addForm = new FormGroup({
    startedAt: new FormControl(null, [Validators.required]),
    person: new FormControl(null, [Validators.required])
  }, { updateOn: 'submit' });

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      created: $localize`:@@session.create.success:Session was created successfully.`
    };
    return messages[key];
  }

  ngOnInit() {
    this.sessionService.getPersonValues()
        .subscribe({
          next: (data) => this.personValues = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  handleSubmit() {
    window.scrollTo(0, 0);
    this.addForm.markAllAsTouched();
    if (!this.addForm.valid) {
      return;
    }
    const data = new SessionDTO(this.addForm.value);
    this.sessionService.createSession(data)
        .subscribe({
          next: () => this.router.navigate(['/sessions'], {
            state: {
              msgSuccess: this.getMessage('created')
            }
          }),
          error: (error) => this.errorHandler.handleServerError(error.error, this.addForm, this.getMessage)
        });
  }

}
