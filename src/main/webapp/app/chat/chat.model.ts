export class ChatDTO {

  constructor(data:Partial<ChatDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  message?: string|null;
  response?: string|null;
  session?: number|null;

}
