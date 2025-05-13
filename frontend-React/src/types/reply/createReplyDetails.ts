export interface CreateReplyDetails {
    userId: number;
    tweetId: number;
    parentReplyId?: number | null;
    content: string;
    createdAt: string;
}
