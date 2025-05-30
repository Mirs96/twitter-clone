package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.TrendingHashtagDto;
import com.twitterclone.backend.model.TrendingHashtag;
import com.twitterclone.backend.model.services.HashtagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/hashtag")
@RequiredArgsConstructor
@Tag(name = "Hashtags")
public class HashtagController {
    private final HashtagService hashtagService;

    @Operation(summary = "Get top trending hashtags")
    @GetMapping("/trending")
    public ResponseEntity<List<TrendingHashtagDto>> getTrendingHashtags() {
        List<TrendingHashtag> trendingHashtags = hashtagService.getTopTrendingHashtags();
        List<TrendingHashtagDto> dtoList = trendingHashtags.stream()
                .map(TrendingHashtagDto::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "Get paginated list of trending hashtags")
    @GetMapping("/trending/paged")
    public ResponseEntity<Page<TrendingHashtagDto>> getPaginatedTrendingHashtags(
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        Page<TrendingHashtag> trendingHashtagsPage = hashtagService.getPaginatedTrending(PageRequest.of(page, size));
        Page<TrendingHashtagDto> dtoListPage = trendingHashtagsPage.map(TrendingHashtagDto::new);
        return ResponseEntity.ok(dtoListPage);
    }
}
