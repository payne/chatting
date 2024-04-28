import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { InputRowComponent } from 'app/common/input-row/input-row.component';
import { ChatService } from 'app/chat/chat.service';
import { ChatDTO } from 'app/chat/chat.model';
import { ErrorHandler } from 'app/common/error-handler.injectable';


@Component({
  selector: 'app-chat-add',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, InputRowComponent],
  templateUrl: './chat-add.component.html'
})
export class ChatAddComponent implements OnInit {

  chatService = inject(ChatService);
  router = inject(Router);
  errorHandler = inject(ErrorHandler);

  sessionValues?: Map<number,string>;

  addForm = new FormGroup({
    message: new FormControl(null, [Validators.required]),
    response: new FormControl(null, [Validators.required]),
    session: new FormControl(null, [Validators.required])
  }, { updateOn: 'submit' });

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      created: $localize`:@@chat.create.success:Chat was created successfully.`
    };
    return messages[key];
  }

  ngOnInit() {
    this.chatService.getSessionValues()
        .subscribe({
          next: (data) => this.sessionValues = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  handleSubmit() {
    window.scrollTo(0, 0);
    this.addForm.markAllAsTouched();
    if (!this.addForm.valid) {
      return;
    }
    const data = new ChatDTO(this.addForm.value);
    this.chatService.createChat(data)
        .subscribe({
          next: () => this.router.navigate(['/chats'], {
            state: {
              msgSuccess: this.getMessage('created')
            }
          }),
          error: (error) => this.errorHandler.handleServerError(error.error, this.addForm, this.getMessage)
        });
  }

}
