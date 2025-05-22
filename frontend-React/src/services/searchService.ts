import { AutocompleteResponse } from "../types/search/autocompleteResponse";
import { customFetch } from "../utils/api";

const urlExtension = '/search';

export const autocompleteSearch = (query: string): Promise<AutocompleteResponse> => {
    const params = new URLSearchParams({ query });
    return customFetch<AutocompleteResponse>(`${urlExtension}/autocomplete?${params.toString()}`);
};