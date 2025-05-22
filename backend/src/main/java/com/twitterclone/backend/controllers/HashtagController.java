package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.TrendingHashtagDto;
import com.twitterclone.backend.model.TrendingHashtag;
import com.twitterclone.backend.model.services.HashtagService;
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
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping("/trending")
    public ResponseEntity<List<TrendingHashtagDto>> getTrendingHashtags() {
        List<TrendingHashtag> trendingHashtags = hashtagService.getTopTrendingHashtags();
        List<TrendingHashtagDto> dtoList = trendingHashtags.stream()
                                                              .map(TrendingHashtagDto::new)
                                                              .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/trending/paged")
    public ResponseEntity<Page<TrendingHashtagDto>> getPaginatedTrendingHashtags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TrendingHashtag> trendingHashtagsPage = hashtagService.getPaginatedTrending(PageRequest.of(page, size));
        Page<TrendingHashtagDto> dtoListPage = trendingHashtagsPage.map(TrendingHashtagDto::new);
        return ResponseEntity.ok(dtoListPage);
    }
}
