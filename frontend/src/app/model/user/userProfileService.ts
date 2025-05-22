import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, BehaviorSubject, tap } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { UserProfileDetails } from "./userProfileDetails";
import { FollowedUserDetails } from "./FollowedUserDetails";
import { FollowerUserDetails } from "./FollowerUserDetails";
import { UserDetails } from "./userDetails";

@Injectable({
    providedIn: 'root'
})
export class UserProfileService {
    private urlExtension = '/user';

    constructor(private http: HttpClient) { }

    getUserProfile(userProfileId: number): Observable<UserProfileDetails> {
        return this.http.get<UserProfileDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${userProfileId}/profile`);
    }
    
    updateProfile(formData: FormData, userId: number): Observable<void> {
        return this.http.post<void>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/update-profile`, formData);
    }      

    follow(userIdToFollow: number): Observable<void> {
        return this.http.post<void>(`${HttpConfig.apiUrl}${this.urlExtension}/follow/${userIdToFollow}`, {});
    }

    unfollow(userIdToUnfollow: number): Observable<void> {
        return this.http.delete<void>(`${HttpConfig.apiUrl}${this.urlExtension}/unfollow/${userIdToUnfollow}`);
    }

    findFollowingByUserId(userId: number): Observable<FollowedUserDetails[]> {
        return this.http.get<FollowedUserDetails[]>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/followed`);
    }

    findFollowersByUserId(userId: number): Observable<FollowerUserDetails[]> {
        return this.http.get<FollowerUserDetails[]>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/followers`);
    }
}