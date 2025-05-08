export interface CreateReplyDetails {
    userId: number;
    tweetId: number;
    parentReplyId?: number | null;
    content: string;
    creationDate: string;
    creationTime: string;
}
