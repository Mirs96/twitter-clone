export interface CreateReplyDetails {
    userId: number;
    tweetId: number;
    parentReplyId?: number;
    content: string;
}