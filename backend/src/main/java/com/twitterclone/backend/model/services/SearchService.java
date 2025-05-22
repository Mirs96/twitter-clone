package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.AutocompleteResponse;

public interface SearchService {
    AutocompleteResponse autocomplete(String query);
}
