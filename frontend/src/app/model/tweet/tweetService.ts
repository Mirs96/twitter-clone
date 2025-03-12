import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { TweetDetails } from "./tweetDetails";
import { HttpConfig } from "../../config/http-config";
import { Page } from "../page";
import { DisplayTweetDetails } from "./DisplayTweetDetails";


@Injectable({
    providedIn: 'root'
})
export class TweetService {
    urlExtension = '/tweet';
    
    constructor(private http: HttpClient) {}

    createTweet(tweet: TweetDetails): Observable<TweetDetails> {
        return this.http.post<TweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}`, tweet);
    }

    getGeneralTweets(page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
                        .set('page', page.toString())
                        .set('size', size.toString());
        
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/trending`, { params });
    }
}