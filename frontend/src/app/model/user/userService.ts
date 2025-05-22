import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, of } from "rxjs";
import { catchError, tap } from 'rxjs/operators';
import { UserDetails } from "./userDetails";
import { HttpClient } from "@angular/common/http";
import { HttpConfig } from "../../config/http-config";


@Injectable({
    providedIn: 'root',
})
export class UserService {
    private urlExtension = '/user';
    private _userDetails$ = new BehaviorSubject<UserDetails | null>(null);
    private _userStatus$ = new BehaviorSubject<'idle' | 'loading' | 'succeeded' | 'failed'>('idle');
    private _userError$ = new BehaviorSubject<string | null>(null);

    public userDetails$ = this._userDetails$.asObservable();
    public userStatus$ = this._userStatus$.asObservable();
    public userError$ = this._userError$.asObservable();

    constructor(private http: HttpClient) { }

    fetchUserDetails(userId: number): Observable<UserDetails> {
        if (!userId) {
            this._userError$.next('User ID is required');
            this._userStatus$.next('failed');
            return of(); // Or throw error
        }
        this._userStatus$.next('loading');
        this._userError$.next(null);
        return this.http.get<UserDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}`).pipe(
            tap(data => {
                this._userDetails$.next(data);
                this._userStatus$.next('succeeded');
            }),
            catchError(error => {
                const errorMessage = error.message || 'Failed to fetch user details';
                this._userError$.next(errorMessage);
                this._userStatus$.next('failed');
                this._userDetails$.next(null);
                throw error; 
            })
        );
    }

    clearUserState(): void {
        this._userDetails$.next(null);
        this._userStatus$.next('idle');
        this._userError$.next(null);
    }

    getUserDetailsValue(): UserDetails | null {
        return this._userDetails$.value;
    }
}