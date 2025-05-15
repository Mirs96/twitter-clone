export interface DisplayTweetDetails {
    id: number;
    userId: number;
    userNickname: string;
    userProfilePicture: string;
    content: string;
    createdAt: string;
    likeCount: number;
    replyCount: number;
    bookmarkCount: number;
    liked: boolean;
    bookmarked: boolean;
    likeId?: number;
    bookmarkId?: number;
}