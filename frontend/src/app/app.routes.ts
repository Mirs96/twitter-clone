import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AuthHomeComponent } from './authentication/auth-home/auth-home.component';
import { TweetDetailComponent } from './tweet-detail/tweet-detail.component';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path:'home', component: HomeComponent },
    { path: 'tweet/:id', component: TweetDetailComponent }
];
