package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.AutocompleteResponse;
import com.twitterclone.backend.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutocompleteResponseDto {
    private List<UserDto> users;
    private List<HashtagDto> hashtags;

    public AutocompleteResponseDto(AutocompleteResponse autocompleteResponse) {
        this.users = autocompleteResponse.getUsers().stream().map(UserDto::new).toList();
        this.hashtags = autocompleteResponse.getHashtags().stream().map(HashtagDto::new).toList();
    }
}
