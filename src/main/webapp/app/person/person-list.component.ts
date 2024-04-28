import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { ErrorHandler } from 'app/common/error-handler.injectable';
import { PersonService } from 'app/person/person.service';
import { PersonDTO } from 'app/person/person.model';
import { SearchFilterComponent } from 'app/common/list-helper/search-filter.component';
import { SortingComponent } from 'app/common/list-helper/sorting.component';
import { getListParams } from 'app/common/utils';
import { Page, PaginationComponent } from 'app/common/list-helper/pagination.component';


@Component({
  selector: 'app-person-list',
  standalone: true,
  imports: [CommonModule, SearchFilterComponent ,SortingComponent, PaginationComponent, RouterLink],
  templateUrl: './person-list.component.html'})
export class PersonListComponent implements OnInit, OnDestroy {

  personService = inject(PersonService);
  errorHandler = inject(ErrorHandler);
  route = inject(ActivatedRoute);
  router = inject(Router);
  persons?: Page<PersonDTO>;
  navigationSubscription?: Subscription;

  sortOptions = {
    'id,ASC': $localize`:@@person.list.sort.id,ASC:Sort by Id (Ascending)`, 
    'email,ASC': $localize`:@@person.list.sort.email,ASC:Sort by Email (Ascending)`, 
    'firstName,ASC': $localize`:@@person.list.sort.firstName,ASC:Sort by First Name (Ascending)`
  }

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      confirm: $localize`:@@delete.confirm:Do you really want to delete this element? This cannot be undone.`,
      deleted: $localize`:@@person.delete.success:Person was removed successfully.`,
      'person.session.person.referenced': $localize`:@@person.session.person.referenced:This entity is still referenced by Session ${details?.id} via field Person.`
    };
    return messages[key];
  }

  ngOnInit() {
    this.loadData();
    this.navigationSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.loadData();
      }
    });
  }

  ngOnDestroy() {
    this.navigationSubscription!.unsubscribe();
  }
  
  loadData() {
    this.personService.getAllPersons(getListParams(this.route))
        .subscribe({
          next: (data) => this.persons = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  confirmDelete(id: number) {
    if (confirm(this.getMessage('confirm'))) {
      this.personService.deletePerson(id)
          .subscribe({
            next: () => this.router.navigate(['/people'], {
              state: {
                msgInfo: this.getMessage('deleted')
              }
            }),
            error: (error) => {
              if (error.error?.code === 'REFERENCED') {
                const messageParts = error.error.message.split(',');
                this.router.navigate(['/people'], {
                  state: {
                    msgError: this.getMessage(messageParts[0], { id: messageParts[1] })
                  }
                });
                return;
              }
              this.errorHandler.handleServerError(error.error)
            }
          });
    }
  }

}
