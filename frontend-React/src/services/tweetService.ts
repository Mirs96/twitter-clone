import { Page } from "../types/page";
import { BookmarkDetails } from "../types/tweet/bookmarkDetails";
import { CreateTweetDetails } from "../types/tweet/createTweetDetails";
import { DisplayTweetDetails } from "../types/tweet/displayTweetDetails";
import { LikeTweetDetails } from "../types/tweet/likeTweetDetails";
import { customFetch } from "../utils/api";

const urlExtension = '/tweet';

export const createTweet = (tweet: CreateTweetDetails): Promise<DisplayTweetDetails> => {
    return customFetch<DisplayTweetDetails>(`${urlExtension}`, {
        method: 'POST',
        body: JSON.stringify(tweet)
    });
};

export const findTweetById = (tweetId: number): Promise<DisplayTweetDetails> => {
    return customFetch<DisplayTweetDetails>(`${urlExtension}/${tweetId}`);
};


export const getTweets = (page: number, size: number): Promise<Page<DisplayTweetDetails>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    return customFetch<Page<DisplayTweetDetails>>(`${urlExtension}/trending?${params.toString()}`);
};


export const getFollowedUsersTweets = (page: number, size: number): Promise<Page<DisplayTweetDetails>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    return customFetch<Page<DisplayTweetDetails>>(`${urlExtension}/followed?${params.toString()}`);
};


export const getTweetsByUserId = (userId: number, page: number, size: number): Promise<Page<DisplayTweetDetails>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    return customFetch<Page<DisplayTweetDetails>>(`${urlExtension}/${userId}/user?${params.toString()}`);
};

export const addLikeToTweet = (like: LikeTweetDetails): Promise<{ likeCount: number, likeId: number }> => {
    return customFetch<{ likeCount: number, likeId: number }>(`${urlExtension}/like`, {
        method: 'POST',
        body: JSON.stringify(like)
    });
};

export const removeLikeFromTweet = (likeId: number): Promise<{ likeCount: number }> => {
    return customFetch<{ likeCount: number }>(`${urlExtension}/${likeId}/like`, { method: 'DELETE' });
};

export const addBookmarkToTweet = (bookmark: BookmarkDetails): Promise<{ bookmarkCount: number, bookmarkId: number }> => {
    return customFetch<{ bookmarkCount: number, bookmarkId: number }>(`${urlExtension}/bookmark`, {
        method: 'POST',
        body: JSON.stringify(bookmark)
    });
};

export const removeBookmarkFromTweet = (bookmarkId: number): Promise<{ bookmarkCount: number }> => {
    return customFetch<{ bookmarkCount: number }>(`${urlExtension}/${bookmarkId}/bookmark`, { method: 'DELETE' });
};
