import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { InputRowComponent } from 'app/common/input-row/input-row.component';
import { PersonService } from 'app/person/person.service';
import { PersonDTO } from 'app/person/person.model';
import { ErrorHandler } from 'app/common/error-handler.injectable';


@Component({
  selector: 'app-person-add',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, InputRowComponent],
  templateUrl: './person-add.component.html'
})
export class PersonAddComponent {

  personService = inject(PersonService);
  router = inject(Router);
  errorHandler = inject(ErrorHandler);

  addForm = new FormGroup({
    email: new FormControl(null, [Validators.required, Validators.maxLength(255)]),
    firstName: new FormControl(null, [Validators.maxLength(255)]),
    lastName: new FormControl(null, [Validators.maxLength(255)])
  }, { updateOn: 'submit' });

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      created: $localize`:@@person.create.success:Person was created successfully.`,
      PERSON_EMAIL_UNIQUE: $localize`:@@Exists.person.email:This Email is already taken.`
    };
    return messages[key];
  }

  handleSubmit() {
    window.scrollTo(0, 0);
    this.addForm.markAllAsTouched();
    if (!this.addForm.valid) {
      return;
    }
    const data = new PersonDTO(this.addForm.value);
    this.personService.createPerson(data)
        .subscribe({
          next: () => this.router.navigate(['/people'], {
            state: {
              msgSuccess: this.getMessage('created')
            }
          }),
          error: (error) => this.errorHandler.handleServerError(error.error, this.addForm, this.getMessage)
        });
  }

}
