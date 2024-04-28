import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { PersonListComponent } from './person/person-list.component';
import { PersonAddComponent } from './person/person-add.component';
import { PersonEditComponent } from './person/person-edit.component';
import { ChatListComponent } from './chat/chat-list.component';
import { ChatAddComponent } from './chat/chat-add.component';
import { ChatEditComponent } from './chat/chat-edit.component';
import { SessionListComponent } from './session/session-list.component';
import { SessionAddComponent } from './session/session-add.component';
import { SessionEditComponent } from './session/session-edit.component';
import { ErrorComponent } from './error/error.component';


export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: $localize`:@@home.index.headline:Welcome to your new app!`
  },
  {
    path: 'people',
    component: PersonListComponent,
    title: $localize`:@@person.list.headline:Persons`
  },
  {
    path: 'people/add',
    component: PersonAddComponent,
    title: $localize`:@@person.add.headline:Add Person`
  },
  {
    path: 'people/edit/:id',
    component: PersonEditComponent,
    title: $localize`:@@person.edit.headline:Edit Person`
  },
  {
    path: 'chats',
    component: ChatListComponent,
    title: $localize`:@@chat.list.headline:Chats`
  },
  {
    path: 'chats/add',
    component: ChatAddComponent,
    title: $localize`:@@chat.add.headline:Add Chat`
  },
  {
    path: 'chats/edit/:id',
    component: ChatEditComponent,
    title: $localize`:@@chat.edit.headline:Edit Chat`
  },
  {
    path: 'sessions',
    component: SessionListComponent,
    title: $localize`:@@session.list.headline:Sessions`
  },
  {
    path: 'sessions/add',
    component: SessionAddComponent,
    title: $localize`:@@session.add.headline:Add Session`
  },
  {
    path: 'sessions/edit/:id',
    component: SessionEditComponent,
    title: $localize`:@@session.edit.headline:Edit Session`
  },
  {
    path: 'error',
    component: ErrorComponent,
    title: $localize`:@@error.headline:Error`
  },
  {
    path: '**',
    component: ErrorComponent,
    title: $localize`:@@notFound.headline:Page not found`
  }
];
