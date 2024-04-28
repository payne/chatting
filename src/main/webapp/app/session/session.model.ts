export class SessionDTO {

  constructor(data:Partial<SessionDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  startedAt?: string|null;
  person?: number|null;

}
