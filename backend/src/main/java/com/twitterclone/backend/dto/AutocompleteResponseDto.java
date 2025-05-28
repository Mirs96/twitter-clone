package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.AutocompleteResponse;
import com.twitterclone.backend.model.entities.User; // Not used directly, but kept for context
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for autocomplete search queries, containing lists of matching users and hashtags.")
public class AutocompleteResponseDto {

    @Schema(description = "List of users matching the search query.")
    private List<UserDto> users;

    @Schema(description = "List of hashtags matching the search query.")
    private List<HashtagDto> hashtags;

    public AutocompleteResponseDto(AutocompleteResponse autocompleteResponse) {
        this.users = autocompleteResponse.getUsers().stream().map(UserDto::new).toList();
        this.hashtags = autocompleteResponse.getHashtags().stream().map(HashtagDto::new).toList();
    }
}