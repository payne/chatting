import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { ChatDTO } from 'app/chat/chat.model';
import { Page } from 'app/common/list-helper/pagination.component';
import { map } from 'rxjs';
import { transformRecordToMap } from 'app/common/utils';


@Injectable({
  providedIn: 'root',
})
export class ChatService {

  http = inject(HttpClient);
  resourcePath = environment.apiPath + '/api/chats';

  getAllChats(params?: Record<string,string>) {
    return this.http.get<Page<ChatDTO>>(this.resourcePath, { params });
  }

  getChat(id: number) {
    return this.http.get<ChatDTO>(this.resourcePath + '/' + id);
  }

  createChat(chatDTO: ChatDTO) {
    return this.http.post<number>(this.resourcePath, chatDTO);
  }

  updateChat(id: number, chatDTO: ChatDTO) {
    return this.http.put<number>(this.resourcePath + '/' + id, chatDTO);
  }

  deleteChat(id: number) {
    return this.http.delete(this.resourcePath + '/' + id);
  }

  getSessionValues() {
    return this.http.get<Record<string,number>>(this.resourcePath + '/sessionValues')
        .pipe(map(transformRecordToMap));
  }

}
