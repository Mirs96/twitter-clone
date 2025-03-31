import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { CreateReplyDetails } from "./createReplyDetails";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { HttpConfig } from "../../config/http-config";
import { ReplyDetails } from "./replyDetails";
import { Page } from "../page";
import { LikeReplyDetails } from "./likeReplyDetails";

@Injectable({
    providedIn: 'root'
})
export class ReplyService {
    urlExtension = '/reply';

    constructor(private http: HttpClient) { }

    replyToTweet(reply: CreateReplyDetails): Observable<ReplyDetails> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        return this.http.post<ReplyDetails>(`${HttpConfig.apiUrl}${this.urlExtension}`, reply, { headers });
    }

    getMainRepliesByTweetId(tweetId: number, page: number, size: number): Observable<Page<ReplyDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<ReplyDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${tweetId}/tweet`, { params });
    }

    getNestedRepliesByParentReplyId(replyId: number): Observable<ReplyDetails[]> {
        return this.http.get<ReplyDetails[]>(`${HttpConfig.apiUrl}${this.urlExtension}/${replyId}/nested`);
    }

    removeReplyFromTweet(tweetId: number): Observable<void> {
        return this.http.delete<void>(`${HttpConfig.apiUrl}${this.urlExtension}/${tweetId}`);
    }

    findReplyById(tweetId: number): Observable<ReplyDetails> {
        return this.http.get<ReplyDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${tweetId}`);
    }

    addLikeToReply(like: LikeReplyDetails): Observable<ReplyDetails> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        return this.http.post<ReplyDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/like`, like, { headers });
    }

    removeLikeFromReply(likeId?: number): Observable<ReplyDetails> {
        return this.http.delete<ReplyDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${likeId}/like`);
    }
}