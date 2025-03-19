export interface CreateReplyDetails {
    id: number;
    userId: number;
    tweetId: number;
    parentReplyId?: number;
    content: string;
    creationDate: string;
    creationTime: string;
}