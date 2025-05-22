package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.AutocompleteResponseDto;
import com.twitterclone.backend.model.AutocompleteResponse;
import com.twitterclone.backend.model.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/autocomplete")
    public ResponseEntity<AutocompleteResponseDto> autocomplete(@RequestParam String query) {
        AutocompleteResponse response = searchService.autocomplete(query);
        return ResponseEntity.ok(new AutocompleteResponseDto(response));
    }
}
