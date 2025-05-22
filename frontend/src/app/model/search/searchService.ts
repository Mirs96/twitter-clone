import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { AutocompleteResponse } from "./autocompleteResponse";

@Injectable({
    providedIn: 'root'
})
export class SearchService {
    private urlExtension = '/search';

    constructor(private http: HttpClient) { }

    autocompleteSearch(query: string): Observable<AutocompleteResponse> {
        const params = new HttpParams().set('query', query);
        return this.http.get<AutocompleteResponse>(`${HttpConfig.apiUrl}${this.urlExtension}/autocomplete`, { params });
    }
}