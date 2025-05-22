import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { TweetDetailComponent } from './tweet/tweet-detail/tweet-detail.component';
import { UserProfileComponent } from './user/user-profile/user-profile.component';
import { ExploreComponent } from './explore/explore.component';
import { BookmarksComponent } from './bookmarks/bookmarks.component';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path:'home', component: HomeComponent },
    { path: 'tweet/:id', component: TweetDetailComponent },
    { path: 'profile/:id', component: UserProfileComponent},
    { path: 'explore', component: ExploreComponent },
    { path: 'explore/tag/:hashtagId/:hashtagName', component: ExploreComponent },
    { path: 'notifications', component: HomeComponent }, // Placeholder, adjust as needed
    { path: 'bookmarks', component: BookmarksComponent }
];
