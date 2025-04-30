import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { UserProfileDetails } from "./userProfileDetails";
import { FollowedUserDetails } from "./FollowedUserDetails";
import { FollowerUserDetails } from "./FollowerUserDto";

@Injectable({
    providedIn: 'root'
})
export class UserProfileService {
    urlExtension = '/user';

    constructor(private http: HttpClient) { }

    getUserProfile(userProfileId: number): Observable<UserProfileDetails> {
        return this.http.get<UserProfileDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${userProfileId}/profile`);
    }
    
    updateProfile(formData: FormData): Observable<void> {
        return this.http.post<void>(`${HttpConfig.apiUrl}${this.urlExtension}/update-profile`, formData);
    }      

    follow(userIdToFollow: number): Observable<void> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        return this.http.post<void>(`${HttpConfig.apiUrl}${this.urlExtension}/follow/${userIdToFollow}`, { headers });
    }

    unfollow(userIdToUnfollow: number): Observable<void> {
        return this.http.delete<void>(`${HttpConfig.apiUrl}${this.urlExtension}/unfollow/${userIdToUnfollow}`);
    }

    findFollowingByUserId(userId: number): Observable<FollowedUserDetails[]> {
        return this.http.get<FollowedUserDetails[]>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/followed`);
    }

    findFollowersByUserId(userId: number): Observable<FollowerUserDetails[]> {
        return this.http.get<FollowerUserDetails[]>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/followers`)
    }
}