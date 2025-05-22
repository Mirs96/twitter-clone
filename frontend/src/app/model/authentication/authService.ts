import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { TokenResponse } from "./tokenResponse";
import { LoginDetails } from "./loginDetails";
import { Observable, BehaviorSubject, tap } from "rxjs";
import { HttpConfig } from "../../config/http-config";
import { RegisterPayload } from "./registerDetails";
import { jwtDecode, JwtPayload } from 'jwt-decode';

interface DecodedToken extends JwtPayload {
    userId: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private urlExtension = '/auth';
    private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
    private _userId$ = new BehaviorSubject<number | null>(null);
    private tokenKey = 'jwtToken';

    public isLoggedIn$ = this._isLoggedIn$.asObservable();
    public userId$ = this._userId$.asObservable();

    constructor(private http: HttpClient) {
        this.checkAuthStatus();
    }

    login(loginDetails: LoginDetails): Observable<TokenResponse> {
        return this.http.post<TokenResponse>(`${HttpConfig.apiUrl}${this.urlExtension}/login`, loginDetails).pipe(
            tap(response => this.setAuth(response.token))
        );
    }

    register(registerPayload: RegisterPayload | FormData): Observable<TokenResponse> {
        const headers = registerPayload instanceof FormData ? undefined : new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.post<TokenResponse>(`${HttpConfig.apiUrl}${this.urlExtension}/register`, registerPayload, { headers }).pipe(
            tap(response => this.setAuth(response.token))
        );
    }

    setAuth(token: string): void {
        localStorage.setItem(this.tokenKey, token);
        this.updateAuthState(token);
    }

    clearAuth(): void {
        localStorage.removeItem(this.tokenKey);
        this.updateAuthState(null);
    }

    checkAuthStatus(): void {
        const token = localStorage.getItem(this.tokenKey);
        this.updateAuthState(token);
    }

    private updateAuthState(token: string | null): void {
        if (token) {
            try {
                const decodedToken = jwtDecode<DecodedToken>(token);
                const currentTime = Math.floor(Date.now() / 1000);
                if (decodedToken.exp && decodedToken.exp > currentTime) {
                    this._isLoggedIn$.next(true);
                    this._userId$.next(parseInt(decodedToken.userId, 10));
                    return;
                }
            } catch (error) {
                console.error('Error decoding token:', error);
            }
        }
        this._isLoggedIn$.next(false);
        this._userId$.next(null);
        if (token) { // If token was present but invalid/expired, remove it
             localStorage.removeItem(this.tokenKey);
        }
    }

    isLoggedIn(): boolean {
        return this._isLoggedIn$.value;
    }

    getUserId(): number | null {
        return this._userId$.value;
    }

    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }
}