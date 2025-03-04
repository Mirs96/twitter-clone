import { Injectable } from "@angular/core";
import { jwtDecode } from "jwt-decode";
import { BehaviorSubject } from "rxjs";

@Injectable({
    providedIn: 'root',
})
export class UserService {
    private loggedIn = new BehaviorSubject<boolean>(this.checkToken()); // Inizializza con il token
    loggedIn$ = this.loggedIn.asObservable();

    constructor() {}

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
}