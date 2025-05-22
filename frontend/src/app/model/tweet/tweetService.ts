import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CreateTweetDetails } from "./createTweetDetails";
import { HttpConfig } from "../../config/http-config";
import { Page } from "../page";
import { LikeTweetDetails } from "./likeTweetDetails";
import { DisplayTweetDetails } from "./displayTweetDetails";
import { BookmarkDetails } from "./bookmarkDetails";

@Injectable({
    providedIn: 'root'
})
export class TweetService {
    private urlExtension = '/tweet';

    constructor(private http: HttpClient) { }

    createTweet(tweet: CreateTweetDetails): Observable<DisplayTweetDetails> {
        return this.http.post<DisplayTweetDetails>(`${HttpConfig.apiUrl}${this.urlExtension}`, tweet);
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

    getFollowedUsersTweets(page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/followed`, { params });
    }

    getTweetsByUserId(userId: number, page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/user`, { params });
    }

    addLikeToTweet(like: LikeTweetDetails): Observable<{ likeCount: number, likeId: number }> {
        return this.http.post<{ likeCount: number, likeId: number }>(`${HttpConfig.apiUrl}${this.urlExtension}/like`, like);
    }

    removeLikeFromTweet(likeId: number): Observable<{ likeCount: number }> {
        return this.http.delete<{ likeCount: number }>(`${HttpConfig.apiUrl}${this.urlExtension}/${likeId}/like`);
    }
    
    addBookmarkToTweet(bookmark: BookmarkDetails): Observable<{ bookmarkCount: number, bookmarkId: number }> {
        return this.http.post<{ bookmarkCount: number, bookmarkId: number }>(`${HttpConfig.apiUrl}${this.urlExtension}/bookmark`, bookmark);
    }

    removeBookmarkFromTweet(bookmarkId: number): Observable<{ bookmarkCount: number }> {
        return this.http.delete<{ bookmarkCount: number }>(`${HttpConfig.apiUrl}${this.urlExtension}/${bookmarkId}/bookmark`);
    }

    getBookmarkedTweets(userId: number, page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/bookmarks`, { params });
    }

    getTweetsByHashtagId(hashtagId: number, page: number, size: number): Observable<Page<DisplayTweetDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<DisplayTweetDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${hashtagId}/hashtag`, { params });
    }
}