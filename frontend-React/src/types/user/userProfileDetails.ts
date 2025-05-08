export interface UserProfileDetails {
    nickname: string;
    profilePicture: string;
    bio: string;
    followersCount: number;
    followingCount: number;
    isFollowing: boolean; // Indicates if the *logged-in* user is following this profile
}