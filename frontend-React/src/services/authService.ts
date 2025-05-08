import { LoginDetails } from "../types/authentication/loginDetails";
import { RegisterPayload } from "../types/authentication/registerDetails";
import { TokenResponse } from "../types/authentication/tokenResponse";
import { customFetch } from "../utils/api";

const urlExtension = '/auth';

export const login = (loginDetails: LoginDetails): Promise<TokenResponse> => {
    return customFetch<TokenResponse>(`${urlExtension}/login`, {
        method: 'POST',
        body: JSON.stringify(loginDetails)
    });
};


export const register = (registerPayload: RegisterPayload | FormData): Promise<TokenResponse> => {
    const isFormData = registerPayload instanceof FormData;
    return customFetch<TokenResponse>(`${urlExtension}/register`, {
        method: 'POST',
        body: isFormData ? registerPayload : JSON.stringify(registerPayload),
        headers: isFormData ? {} : { 'Content-Type': 'application/json' } 
    });
};
