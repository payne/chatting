import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { ErrorHandler } from 'app/common/error-handler.injectable';
import { SessionService } from 'app/session/session.service';
import { SessionDTO } from 'app/session/session.model';
import { SearchFilterComponent } from 'app/common/list-helper/search-filter.component';
import { SortingComponent } from 'app/common/list-helper/sorting.component';
import { getListParams } from 'app/common/utils';
import { Page, PaginationComponent } from 'app/common/list-helper/pagination.component';


@Component({
  selector: 'app-session-list',
  standalone: true,
  imports: [CommonModule, SearchFilterComponent ,SortingComponent, PaginationComponent, RouterLink],
  templateUrl: './session-list.component.html'})
export class SessionListComponent implements OnInit, OnDestroy {

  sessionService = inject(SessionService);
  errorHandler = inject(ErrorHandler);
  route = inject(ActivatedRoute);
  router = inject(Router);
  sessions?: Page<SessionDTO>;
  navigationSubscription?: Subscription;

  sortOptions = {
    'id,ASC': $localize`:@@session.list.sort.id,ASC:Sort by Id (Ascending)`, 
    'startedAt,ASC': $localize`:@@session.list.sort.startedAt,ASC:Sort by Started At (Ascending)`
  }

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      confirm: $localize`:@@delete.confirm:Do you really want to delete this element? This cannot be undone.`,
      deleted: $localize`:@@session.delete.success:Session was removed successfully.`,
      'session.chat.session.referenced': $localize`:@@session.chat.session.referenced:This entity is still referenced by Chat ${details?.id} via field Session.`
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
    this.sessionService.getAllSessions(getListParams(this.route))
        .subscribe({
          next: (data) => this.sessions = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  confirmDelete(id: number) {
    if (confirm(this.getMessage('confirm'))) {
      this.sessionService.deleteSession(id)
          .subscribe({
            next: () => this.router.navigate(['/sessions'], {
              state: {
                msgInfo: this.getMessage('deleted')
              }
            }),
            error: (error) => {
              if (error.error?.code === 'REFERENCED') {
                const messageParts = error.error.message.split(',');
                this.router.navigate(['/sessions'], {
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
