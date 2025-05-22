package com.twitterclone.backend.model.services;

import com.twitterclone.backend.dto.UserDto;
import com.twitterclone.backend.model.AutocompleteResponse;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.repositories.HashtagRepositoryJpa;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceJpa implements SearchService {

    private final UserRepositoryJpa userRepository;
    private final HashtagRepositoryJpa hashtagRepository;

    private static final int AUTOCOMPLETE_LIMIT = 5;

    @Override
    public AutocompleteResponse autocomplete(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new AutocompleteResponse(List.of(), List.of());
        }

        PageRequest userPageRequest = PageRequest.of(0, 5, Sort.by("nickname"));
        List<User> users = userRepository.findByNicknameStartsWithIgnoreCase(query, userPageRequest);

        PageRequest hashtagPageRequest = PageRequest.of(0, AUTOCOMPLETE_LIMIT, Sort.by("tag"));
        String hashtagQuery = query.startsWith("#") ? query.substring(1) : query;
        List<Hashtag> hashtags = hashtagRepository.findByTagStartsWithIgnoreCase(hashtagQuery, hashtagPageRequest);

        return new AutocompleteResponse(users, hashtags);
    }
}
