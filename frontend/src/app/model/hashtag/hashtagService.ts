import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { Page } from "../page";
import { TrendingHashtagDetails } from "./trendingHashtagDetails";

@Injectable({
    providedIn: 'root'
})
export class HashtagService {
    private urlExtension = '/hashtag';

    constructor(private http: HttpClient) { }

    getTrendingHashtags(): Observable<TrendingHashtagDetails[]> {
        return this.http.get<TrendingHashtagDetails[]>(`${HttpConfig.apiUrl}${this.urlExtension}/trending`);
    }

    getPaginatedTrendingHashtags(page: number, size: number): Observable<Page<TrendingHashtagDetails>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<Page<TrendingHashtagDetails>>(`${HttpConfig.apiUrl}${this.urlExtension}/trending/paged`, { params });
    }
}