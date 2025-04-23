import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { TweetDetails } from "./tweetDetails";
import { HttpConfig } from "../../config/http-config";
import { Page } from "../page";
import { LikeTweetDetails } from "./likeTweetDetails";
import { DisplayTweetDetails } from "./displayTweetDetails";
import { BookmarkDetails } from "./bookmarkDetails";

@Injectable({
    providedIn: 'root'
})
export class TweetService {
    urlExtension = '/tweet';

    constructor(private http: HttpClient) { }

    createTweet(tweet: TweetDetails): Observable<TweetDetails> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        return this.http.post<TweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}`, tweet, { headers });
    }

    findTweetById(tweetId: number): Observable<DisplayTweetDetails> {
        return this.http.get<DisplayTweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${tweetId}`);
    } 

    getTweets(page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/trending`, { params });
    }

    addLikeToTweet(like: LikeTweetDetails): Observable<DisplayTweetDetails> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        return this.http.post<DisplayTweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/like`, like, { headers });
    }

    removeLikeFromTweet(likeId?: number): Observable<DisplayTweetDetails> {
        return this.http.delete<DisplayTweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${likeId}/like`);
    }
    
    addBookmarkToTweet(bookmark: BookmarkDetails): Observable<DisplayTweetDetails> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        return this.http.post<DisplayTweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/bookmark`, bookmark, { headers });
    }

    removeBookmarkFromTweet(bookmarkId?: number): Observable<DisplayTweetDetails> {
        return this.http.delete<DisplayTweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${bookmarkId}/bookmark`);
    }

    getTweetsByUserId(userId: number, page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/user`, { params });
    }
}