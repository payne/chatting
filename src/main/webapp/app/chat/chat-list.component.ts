import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { ErrorHandler } from 'app/common/error-handler.injectable';
import { ChatService } from 'app/chat/chat.service';
import { ChatDTO } from 'app/chat/chat.model';
import { SearchFilterComponent } from 'app/common/list-helper/search-filter.component';
import { SortingComponent } from 'app/common/list-helper/sorting.component';
import { getListParams } from 'app/common/utils';
import { Page, PaginationComponent } from 'app/common/list-helper/pagination.component';


@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, SearchFilterComponent ,SortingComponent, PaginationComponent, RouterLink],
  templateUrl: './chat-list.component.html'})
export class ChatListComponent implements OnInit, OnDestroy {

  chatService = inject(ChatService);
  errorHandler = inject(ErrorHandler);
  route = inject(ActivatedRoute);
  router = inject(Router);
  chats?: Page<ChatDTO>;
  navigationSubscription?: Subscription;

  sortOptions = {
    'id,ASC': $localize`:@@chat.list.sort.id,ASC:Sort by Id (Ascending)`, 
    'message,ASC': $localize`:@@chat.list.sort.message,ASC:Sort by Message (Ascending)`, 
    'response,ASC': $localize`:@@chat.list.sort.response,ASC:Sort by Response (Ascending)`
  }

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      confirm: $localize`:@@delete.confirm:Do you really want to delete this element? This cannot be undone.`,
      deleted: $localize`:@@chat.delete.success:Chat was removed successfully.`    };
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
    this.chatService.getAllChats(getListParams(this.route))
        .subscribe({
          next: (data) => this.chats = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  confirmDelete(id: number) {
    if (confirm(this.getMessage('confirm'))) {
      this.chatService.deleteChat(id)
          .subscribe({
            next: () => this.router.navigate(['/chats'], {
              state: {
                msgInfo: this.getMessage('deleted')
              }
            }),
            error: (error) => this.errorHandler.handleServerError(error.error)
          });
    }
  }

}
