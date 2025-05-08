import { FollowedUserDetails } from "../types/user/FollowedUserDetails";
import { FollowerUserDetails } from "../types/user/FollowerUserDetails";
import { UserProfileDetails } from "../types/user/userProfileDetails";
import { customFetch } from "../utils/api";

const urlExtension = '/user';

// Note: getUserProfile now gets follow status based on the authenticated user making the request (via JWT)
export const getUserProfile = (userProfileId: number): Promise<UserProfileDetails> => {
    return customFetch<UserProfileDetails>(`${urlExtension}/${userProfileId}/profile`);
};

export const updateProfile = (formData: FormData, userId: number): Promise<void> => {
    // Use POST to allow FormData for file uploads, even though it's an update
    return customFetch<void>(`${urlExtension}/${userId}/update-profile`, {
        method: 'POST', // Changed to POST to simplify FormData handling
        body: formData
    });
};

export const follow = (userIdToFollow: number): Promise<void> => {
    return customFetch<void>(`${urlExtension}/follow/${userIdToFollow}`, {
        method: 'POST'
    });
};

export const unfollow = (userIdToUnfollow: number): Promise<void> => {
    return customFetch<void>(`${urlExtension}/unfollow/${userIdToUnfollow}`, {
        method: 'DELETE'
    });
};

export const findFollowingByUserId = (userId: number): Promise<FollowedUserDetails[]> => {
    return customFetch<FollowedUserDetails[]>(`${urlExtension}/${userId}/followed`);
};

export const findFollowersByUserId = (userId: number): Promise<FollowerUserDetails[]> => {
    return customFetch<FollowerUserDetails[]>(`${urlExtension}/${userId}/followers`);
};
