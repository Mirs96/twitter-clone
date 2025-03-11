import { Injectable } from "@angular/core";
import { jwtDecode } from "jwt-decode";
import { BehaviorSubject } from "rxjs";
import { UserDetails } from "./userDetails";
import { HttpClient } from "@angular/common/http";
import { HttpConfig } from "../../config/http-config";


@Injectable({
    providedIn: 'root',
})
export class UserService {
    urlExtension = '/user';
    // Check if the user is logged
    private loggedIn = new BehaviorSubject<boolean>(this.checkToken());
    loggedIn$ = this.loggedIn.asObservable();

    // Get user data (nickname, id and profile picture)
    private userDetailsSubject = new BehaviorSubject<UserDetails | null>(null);
    userDetails$ = this.userDetailsSubject.asObservable();

    constructor(private http: HttpClient) { }

    private checkToken(): boolean {
        const token = localStorage.getItem('jwtToken');
        if (!token) return false;

        try {
            const decodedToken = jwtDecode<any>(token);
            const currentTime = Math.floor(Date.now() / 1000);
            return decodedToken.exp > currentTime; // True se il token non è scaduto
        } catch (error) {
            console.error("Errore nella decodifica del token", error);
            return false;
        }
    }

    isLoggedIn(): boolean {
        return this.loggedIn.value;
    }

    setLoggedIn(status: boolean): void {
        this.loggedIn.next(status);
    }

    private getDecodedToken() {
        const token = localStorage.getItem('jwtToken'); //serve per leggere il token che è salvato nel local storage
        if (!token) {
            return null;
        }
        try {
            const decodedToken = jwtDecode<any>(token);
            console.log(decodedToken);
            return decodedToken;
        } catch (error) {
            console.error('error decodingToken', error);
            return null;
        }

    }

    getUserIdFromToken(): string | null {
        const dC = this.getDecodedToken();
        if (!dC) {
            return null;
        }
        console.log("decode token esiste", dC);
        return dC.userId;
    }

    fetchUserDetails(): void {
        const userId = this.getUserIdFromToken();
        if (!userId) {
            console.error("User ID not found in token");
            return;
        }

        this.http.get<UserDetails>(`${HttpConfig.apiUrl}${this.urlExtension}/${userId}`).subscribe({
            next: user => this.userDetailsSubject.next(user),
            error: err => console.log(err)
        });
    }

    getUserDetails(): UserDetails | null {
        return this.userDetailsSubject.value;
    }
}