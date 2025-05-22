export interface AutocompleteUser {
    id: number;
    nickname: string;
    profilePicture: string;
}

export interface AutocompleteHashtag {
    id: number;
    tag: string;
}

export interface AutocompleteResponse {
    users: AutocompleteUser[];
    hashtags: AutocompleteHashtag[];
}