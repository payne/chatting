export class PersonDTO {

  constructor(data:Partial<PersonDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  email?: string|null;
  firstName?: string|null;
  lastName?: string|null;

}
