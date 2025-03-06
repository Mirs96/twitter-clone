import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { TokenResponse } from "../authentication/tokenResponse";
import { Observable } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { TweetDetails } from "./tweetDetails";

@Injectable({
    providedIn: 'root'
})
export class TweetService {
    urlExtension = '/tweet';
    
    constructor(private http: HttpClient) {}

    createTweet(tweet: TweetDetails): Observable<TweetDetails> {
        return this.http.post<TweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}`, tweet);
    }
}