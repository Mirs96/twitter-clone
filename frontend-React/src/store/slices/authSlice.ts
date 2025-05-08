import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { jwtDecode } from 'jwt-decode';
import { RootState } from '../store';

interface DecodedToken {
    userId: string;
    exp: number;
}

interface AuthState {
  token: string | null;
  userId: number | null;
  isLoggedIn: boolean;
}

const checkTokenValidity = (token: string | null): { isValid: boolean; decoded: DecodedToken | null } => {
    if (!token) return { isValid: false, decoded: null };
    try {
        const decodedToken = jwtDecode<DecodedToken>(token);
        const currentTime = Math.floor(Date.now() / 1000);
        return { isValid: decodedToken.exp > currentTime, decoded: decodedToken };
    } catch (error) {
        console.error("Error decoding token:", error);
        localStorage.removeItem('jwtToken'); // Remove invalid token
        return { isValid: false, decoded: null };
    }
};

// Initialize state from localStorage
const initialToken = localStorage.getItem('jwtToken');
const { isValid, decoded } = checkTokenValidity(initialToken);

const initialState: AuthState = {
  token: isValid ? initialToken : null,
  userId: isValid && decoded ? parseInt(decoded.userId, 10) : null,
  isLoggedIn: isValid,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAuth: (state, action: PayloadAction<{ token: string }>) => {
      const { token } = action.payload;
      const { isValid, decoded } = checkTokenValidity(token);
      if (isValid && decoded) {
        state.token = token;
        state.userId = parseInt(decoded.userId, 10);
        state.isLoggedIn = true;
        localStorage.setItem('jwtToken', token);
      } else {
        // If token provided is invalid, clear auth state
        state.token = null;
        state.userId = null;
        state.isLoggedIn = false;
        localStorage.removeItem('jwtToken');
        console.error("Attempted to set invalid token.");
      }
    },
    clearAuth: (state) => {
      state.token = null;
      state.userId = null;
      state.isLoggedIn = false;
      localStorage.removeItem('jwtToken');
    },
    // Action to re-validate token from storage on app load (optional, covered by initial state)
    checkAuthStatus: (state) => {
        const token = localStorage.getItem('jwtToken');
        const { isValid, decoded } = checkTokenValidity(token);
        state.token = isValid ? token : null;
        state.userId = isValid && decoded ? parseInt(decoded.userId, 10) : null;
        state.isLoggedIn = isValid;
        if (!isValid && token) {
            localStorage.removeItem('jwtToken'); // Clean up expired token
        }
    }
  },
});

export const { setAuth, clearAuth, checkAuthStatus } = authSlice.actions;

// Selectors
export const selectIsLoggedIn = (state: RootState): boolean => state.auth.isLoggedIn;
export const selectUserId = (state: RootState): number | null => state.auth.userId;
export const selectToken = (state: RootState): string | null => state.auth.token;

export default authSlice.reducer;
