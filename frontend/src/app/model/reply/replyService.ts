import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { CreateReplyDetails } from "./createReplyDetails";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { HttpConfig } from "../../config/http-config";
import { ReplyDetails } from "./replyDetails";
import { Page } from "../page";

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

    getReplyByTweet(tweetId: number, page: number, size: number): Observable<Page<ReplyDetails>> {
            const params = new HttpParams()
                .set('page', page.toString())
                .set('size', size.toString());
        return this.http.get<Page<ReplyDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/${tweetId}`, { params });
    }

    removeReplyFromTweet(tweetId: number): Observable<void> {
        return this.http.delete<void>(`${HttpConfig.apiUrl}${this.urlExtension}/${tweetId}`);
    }


}