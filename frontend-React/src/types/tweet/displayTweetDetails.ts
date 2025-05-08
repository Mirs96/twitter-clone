export interface DisplayTweetDetails {
    id: number;
    userId: number;
    userNickname: string;
    userProfilePicture: string;
    content: string;
    creationDate: string;
    creationTime: string;
    likeCount: number;
    replyCount: number;
    bookmarkCount: number;
    liked: boolean; 
    bookmarked: boolean; 
    likeId?: number; 
    bookmarkId?: number; 
}