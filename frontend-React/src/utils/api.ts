import { HttpConfig } from '../config/http-config';

// Custom error class to handle HTTP errors
export class CustomError extends Error {
    response?: Response;
    data?: string | object;  // Can be either a string or a JSON object

    constructor(message: string, response?: Response, data?: string | object) {
        super(message);
        this.name = 'CustomError';
        this.response = response;
        this.data = data;
    }
}

const getJwtTokenFromStorage = (): string | null => {
    try {
        if (typeof localStorage !== 'undefined') {
            return localStorage.getItem('jwtToken');
        }
        return null;
    } catch (e) {
        return null;
    }
};

// Function to get base authentication headers
const getBaseAuthHeaders = (isFormData: boolean): HeadersInit => {
    const token = getJwtTokenFromStorage();
    const headers: HeadersInit = {};

    if (!isFormData) {
        headers['Content-Type'] = 'application/json';
    }
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
};

// Custom fetch function with error handling
export const customFetch = async <T>(
    url: string,
    options: RequestInit = {}
): Promise<T> => {
    // Determine which headers to use
    const baseHeaders = getBaseAuthHeaders(options.body instanceof FormData);

    let response: Response;
    
    try {
        // Perform the HTTP request
        response = await fetch(`${HttpConfig.apiUrl}${url}`, {
            ...options,
            headers: {
                ...baseHeaders,
                ...options.headers,
            },
        });
    } catch (error: unknown) {
        // Convert network errors in CustomError
        const errorMessage = error instanceof Error ? error.message : 'A network error occurred';
        console.error(`Network error fetching ${HttpConfig.apiUrl}${url}:`, error);
        throw new CustomError(errorMessage, undefined, error instanceof Error ? error : String(error));
    }


    // If the response is not "ok", handle the error
     if (!response.ok) {
        const errorData = await response.text(); 
        console.error(`HTTP error! Status: ${response.status}, Body: ${errorData}`);

        // Try parsing the error data as JSON, fallback to text if it fails
        let parsedError: string | object;
        try {
            parsedError = JSON.parse(errorData);
        } catch (e) {
            parsedError = errorData;
        }

        // Create and throw a custom error object with the response and error data
        throw new CustomError(`HTTP error! Status: ${response.status}`, response, parsedError);
    }

    // If the response doesn't contain JSON or is "no content", return undefined
    const contentType = response.headers.get('content-type');
    if (response.status === 204 || !contentType || !contentType.includes('application/json')) {
        return undefined as T; 
    }

    try {
        return await response.json() as T;
    } catch (error: unknown) {
        // Convert JSON parsing errors in CustomError
        const errorMessage = error instanceof Error ? error.message : 'Failed to parse JSON response';
        console.error(`Failed to parse JSON response from ${response.url}:`, error);
        throw new CustomError(errorMessage, response, error instanceof Error ? error : String(error));
    }
};