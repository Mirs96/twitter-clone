import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { TweetDetailComponent } from './tweet/tweet-detail/tweet-detail.component';
import { UserProfileComponent } from './user/user-profile/user-profile.component';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path:'home', component: HomeComponent },
    { path: 'tweet/:id', component: TweetDetailComponent },
    { path: 'profile/:id', component: UserProfileComponent}
];
