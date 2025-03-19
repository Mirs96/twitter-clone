export interface ReplyDetails {
    id: number;
    userId: number;
    userNickname: string;
    userProfilePicture?: string;
    tweetId: number;
    parentReplyId?: number;
    content: string;
    creationDate: string;
    creationTime: string;
}

