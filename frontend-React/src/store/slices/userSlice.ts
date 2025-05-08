import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { UserDetails } from '../../types/authentication/userDetails';
import { customFetch } from '../../utils/api';
import { RootState } from '../store';

const urlExtension = '/user';

// Define the async thunk for fetching user details
export const fetchUserDetails = createAsyncThunk<UserDetails, number, { rejectValue: string }>(
  'user/fetchDetails',
  async (userId, { rejectWithValue }) => {
    if (!userId) {
      return rejectWithValue('User ID is required');
    }
    try {
      // Fetch basic user details
      const data = await customFetch<UserDetails>(`${urlExtension}/${userId}`);
      return data;
    } catch (error: unknown) {
      return rejectWithValue(error instanceof Error ? 
        error.message || 'Failed to fetch user details' : 'Failed to fetch user details');
    }
  }
);


interface UserState {
  details: UserDetails | null;
  status: 'idle' | 'loading' | 'succeeded' | 'failed';
  error: string | null;
}

const initialState: UserState = {
  details: null,
  status: 'idle',
  error: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    clearUserState: (state) => {
        state.details = null;
        state.status = 'idle';
        state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserDetails.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchUserDetails.fulfilled, (state, action: PayloadAction<UserDetails>) => {
        state.status = 'succeeded';
        state.details = action.payload;
      })
      .addCase(fetchUserDetails.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload || 'Unknown error';
        state.details = null; // Clear details on failure
      });
  },
});

// Export the action creator
export const { clearUserState } = userSlice.actions;

// Export selectors
export const selectUserDetails = (state: RootState): UserDetails | null => state.user.details;
export const selectUserStatus = (state: RootState): 'idle' | 'loading' | 'succeeded' | 'failed' => state.user.status;
export const selectUserError = (state: RootState): string | null => state.user.error;

// Export the reducer
export default userSlice.reducer;
