//package com.twitterclone.backend.controllers;
//
//import com.twitterclone.backend.dto.AutocompleteResponseDto;
//import com.twitterclone.backend.model.AutocompleteResponse;
//import com.twitterclone.backend.model.services.SearchService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
//@RestController
//@RequestMapping("/api/search")
//@RequiredArgsConstructor
//public class SearchController {
//
//    private final SearchService searchService;
//
//    @GetMapping("/autocomplete")
//    public ResponseEntity<AutocompleteResponseDto> autocomplete(@RequestParam String query) {
//        AutocompleteResponse response = searchService.autocomplete(query);
//        return ResponseEntity.ok(new AutocompleteResponseDto(response));
//    }
//}

// File: src/main/java/com/twitterclone/backend/controllers/SearchController.java
package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.AutocompleteResponseDto;
import com.twitterclone.backend.model.AutocompleteResponse;
import com.twitterclone.backend.model.services.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Endpoints for search and autocomplete")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "Get autocomplete suggestions for users and hashtags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved autocomplete suggestions",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AutocompleteResponseDto.class)))
    })
    @GetMapping("/autocomplete")
    public ResponseEntity<AutocompleteResponseDto> autocomplete(
            @Parameter(description = "Search query string") @RequestParam String query) {
        AutocompleteResponse response = searchService.autocomplete(query);
        return ResponseEntity.ok(new AutocompleteResponseDto(response));
    }
}