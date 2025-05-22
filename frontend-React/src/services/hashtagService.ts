import { Page } from "../types/page";
import { TrendingHashtagDetails } from "../types/hashtag/trendingHashtagDetails";
import { customFetch } from "../utils/api";

const urlExtension = '/hashtag';

export const getTrendingHashtags = (): Promise<TrendingHashtagDetails[]> => {
    return customFetch<TrendingHashtagDetails[]>(`${urlExtension}/trending`);
};

export const getPaginatedTrendingHashtags = (page: number, size: number): Promise<Page<TrendingHashtagDetails>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    return customFetch<Page<TrendingHashtagDetails>>(`${urlExtension}/trending/paged?${params.toString()}`);
};