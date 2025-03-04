import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { TokenResponse } from "./tokenResponse";
import { LoginDetails } from "./LoginDetails";
import { Observable } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { RegisterDetails } from "./RegisterDetails";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    urlExtension = '/auth';

    constructor(private http: HttpClient) {}

    login(login: LoginDetails): Observable<TokenResponse> {
        const headers = new HttpHeaders({
            'Content-Type':'application/json'
        });
        return this.http.post<TokenResponse>(`${HttpConfig.apiUrl}${this.urlExtension}/login`, login, {headers});
    }

    register(register: RegisterDetails): Observable<TokenResponse> {
        return this.http.post<TokenResponse>(`${HttpConfig.apiUrl}${this.urlExtension}/register`, register);
    }
}