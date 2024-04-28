import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { PersonDTO } from 'app/person/person.model';
import { Page } from 'app/common/list-helper/pagination.component';


@Injectable({
  providedIn: 'root',
})
export class PersonService {

  http = inject(HttpClient);
  resourcePath = environment.apiPath + '/api/people';

  getAllPersons(params?: Record<string,string>) {
    return this.http.get<Page<PersonDTO>>(this.resourcePath, { params });
  }

  getPerson(id: number) {
    return this.http.get<PersonDTO>(this.resourcePath + '/' + id);
  }

  createPerson(personDTO: PersonDTO) {
    return this.http.post<number>(this.resourcePath, personDTO);
  }

  updatePerson(id: number, personDTO: PersonDTO) {
    return this.http.put<number>(this.resourcePath + '/' + id, personDTO);
  }

  deletePerson(id: number) {
    return this.http.delete(this.resourcePath + '/' + id);
  }

}
