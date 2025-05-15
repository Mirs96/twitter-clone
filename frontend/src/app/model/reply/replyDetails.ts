export interface ReplyDetails {
    id: number;
    userId: number;
    userNickname: string;
    userProfilePicture: string;
    tweetId: number;
    parentReplyId?: number;
    content: string;
    createdAt: string;
    likeCount: number;
    liked: boolean;
    likeId?: number;
    hasNestedReplies?: boolean;
    showNested?: boolean;
}

