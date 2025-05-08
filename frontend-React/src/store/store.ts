import { configureStore } from '@reduxjs/toolkit';
import userReducer from './slices/userSlice';
import authReducer from './slices/authSlice';

const store = configureStore({
  reducer: {
    user: userReducer,
    auth: authReducer, // Add the auth reducer
    // Add other reducers here
  },
});

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>;
// Inferred type: {user: UserState, auth: AuthState, ...}
export type AppDispatch = typeof store.dispatch;

export default store;
