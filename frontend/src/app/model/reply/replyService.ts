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
    private urlExtension = '/reply';

    constructor(private http: HttpClient) { }

    replyToTweet(reply: CreateReplyDetails): Observable<ReplyDetails> {
        return this.http.post<ReplyDetails>(`${HttpConfig.apiUrl}${this.urlExtension}`, reply);
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

    findReplyById(replyId: number): Observable<ReplyDetails> {
        return this.http.get<ReplyDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${replyId}`);
    }

    addLikeToReply(like: LikeReplyDetails): Observable<{ likeCount: number, likeId: number }> {
        return this.http.post<{ likeCount: number, likeId: number }>(`${HttpConfig.apiUrl}${this.urlExtension}/like`, like);
    }

    removeLikeFromReply(likeId: number): Observable<{ likeCount: number }> {
        return this.http.delete<{ likeCount: number }>(`${HttpConfig.apiUrl}${this.urlExtension}/${likeId}/like`);
    }

    getRepliesByUserId(userId: number, page: number, size: number): Observable<Page<ReplyDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<ReplyDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}/user`, { params });
    }
}