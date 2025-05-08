import { Page } from "../types/page";
import { CreateReplyDetails } from "../types/reply/createReplyDetails";
import { LikeReplyDetails } from "../types/reply/likeReplyDetails";
import { ReplyDetails } from "../types/reply/replyDetails";
import { customFetch } from "../utils/api";

const urlExtension = '/reply';

export const replyToTweet = (reply: CreateReplyDetails): Promise<ReplyDetails> => {
    return customFetch<ReplyDetails>(`${urlExtension}`, {
        method: 'POST',
        body: JSON.stringify(reply)
    });
};

export const getMainRepliesByTweetId = (tweetId: number, page: number, size: number): Promise<Page<ReplyDetails>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    return customFetch<Page<ReplyDetails>>(`${urlExtension}/${tweetId}/tweet?${params.toString()}`);
};

export const getNestedRepliesByParentReplyId = (replyId: number): Promise<ReplyDetails[]> => {
    return customFetch<ReplyDetails[]>(`${urlExtension}/${replyId}/nested`);
};

export const removeReplyFromTweet = (tweetId: number): Promise<void> => {
    return customFetch<void>(`${urlExtension}/${tweetId}`, { method: 'DELETE' });
};

export const findReplyById = (replyId: number): Promise<ReplyDetails> => {
    return customFetch<ReplyDetails>(`${urlExtension}/${replyId}`);
};

export const addLikeToReply = (like: LikeReplyDetails): Promise<{ likeCount: number, likeId: number }> => {
    return customFetch<{ likeCount: number, likeId: number }>(`${urlExtension}/like`, {
        method: 'POST',
        body: JSON.stringify(like)
    });
};

export const removeLikeFromReply = (likeId: number): Promise<{ likeCount: number }> => {
    return customFetch<{ likeCount: number }>(`${urlExtension}/${likeId}/like`, { method: 'DELETE' });
};

export const getRepliesByUserId = (userId: number, page: number, size: number): Promise<Page<ReplyDetails>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    return customFetch<Page<ReplyDetails>>(`${urlExtension}/${userId}/user?${params.toString()}`);
};
