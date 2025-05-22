package com.twitterclone.backend.model;

import com.twitterclone.backend.dto.UserDto;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutocompleteResponse {
    private List<User> users;
    private List<Hashtag> hashtags;
}