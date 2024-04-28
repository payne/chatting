import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { SessionDTO } from 'app/session/session.model';
import { Page } from 'app/common/list-helper/pagination.component';
import { map } from 'rxjs';
import { transformRecordToMap } from 'app/common/utils';


@Injectable({
  providedIn: 'root',
})
export class SessionService {

  http = inject(HttpClient);
  resourcePath = environment.apiPath + '/api/sessions';

  getAllSessions(params?: Record<string,string>) {
    return this.http.get<Page<SessionDTO>>(this.resourcePath, { params });
  }

  getSession(id: number) {
    return this.http.get<SessionDTO>(this.resourcePath + '/' + id);
  }

  createSession(sessionDTO: SessionDTO) {
    return this.http.post<number>(this.resourcePath, sessionDTO);
  }

  updateSession(id: number, sessionDTO: SessionDTO) {
    return this.http.put<number>(this.resourcePath + '/' + id, sessionDTO);
  }

  deleteSession(id: number) {
    return this.http.delete(this.resourcePath + '/' + id);
  }

  getPersonValues() {
    return this.http.get<Record<string,string>>(this.resourcePath + '/personValues')
        .pipe(map(transformRecordToMap));
  }

}
