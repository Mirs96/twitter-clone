package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.TrendingHashtag;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.repositories.HashtagRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HashtagServiceJpa implements HashtagService {
    private final HashtagRepositoryJpa hashtagRepo;
    private static final int TOP_TRENDING_LIMIT = 5;
    // Define a default recency window for the paginated popular list in days
    private static final int PAGINATED_POPULAR_RECENCY_DAYS = 90;


    @Override
    @Transactional
    public List<Hashtag> linkHashtagsToTweet(String content) {
        if (content == null || !content.contains("#")) {
            return new ArrayList<>();
        }

        Set<String> tags = new HashSet<>();
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            // group(0) would be "#tag", group(1) is just "tag"
            String tagName = matcher.group(1).toLowerCase();
            if (!tagName.isEmpty()) {
                tags.add(tagName);
            }
        }

        if (tags.isEmpty()) {
            return new ArrayList<>();
        }

        return tags.stream()
                .map(tag -> hashtagRepo.findByTag(tag)
                        .orElseGet(() -> hashtagRepo.save(Hashtag.builder().tag(tag).build())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendingHashtag> getTopTrendingHashtags() {
        List<TrendingHashtag> combinedResults = new ArrayList<>();
        Set<Long> addedHashtagIds = new HashSet<>();
        LocalDateTime now = LocalDateTime.now();

        // Order of preference: 24 hours, 7 days, 30 days, then overall popular (but still recent for overall)
        Pageable limitPageable = PageRequest.of(0, TOP_TRENDING_LIMIT + 5); // Fetch a bit more to ensure distinctness

        List<TrendingHashtag> last24hResults = hashtagRepo.findTrendingHashtagsAndCountSince(now.minusHours(24), limitPageable);
        addDistinctTrendingHashtags(combinedResults, last24hResults, addedHashtagIds, TOP_TRENDING_LIMIT);
        if (combinedResults.size() >= TOP_TRENDING_LIMIT) {
            return combinedResults.subList(0, TOP_TRENDING_LIMIT);
        }

        List<TrendingHashtag> last7dResults = hashtagRepo.findTrendingHashtagsAndCountSince(now.minusDays(7), limitPageable);
        addDistinctTrendingHashtags(combinedResults, last7dResults, addedHashtagIds, TOP_TRENDING_LIMIT);
        if (combinedResults.size() >= TOP_TRENDING_LIMIT) {
            return combinedResults.subList(0, TOP_TRENDING_LIMIT);
        }

        List<TrendingHashtag> last30dResults = hashtagRepo.findTrendingHashtagsAndCountSince(now.minusDays(30), limitPageable);
        addDistinctTrendingHashtags(combinedResults, last30dResults, addedHashtagIds, TOP_TRENDING_LIMIT);
        if (combinedResults.size() >= TOP_TRENDING_LIMIT) {
            return combinedResults.subList(0, TOP_TRENDING_LIMIT);
        }

        // Fallback to generally popular but still active if not enough from shorter periods
        int needed = TOP_TRENDING_LIMIT - combinedResults.size();
        Pageable fallbackPageable = PageRequest.of(0, needed + 5); // Fetch a bit more
        Page<TrendingHashtag> fallbackResults = hashtagRepo.findPopularAndRecentlyActiveHashtags(
                now.minusDays(PAGINATED_POPULAR_RECENCY_DAYS),
                fallbackPageable
        );
        addDistinctTrendingHashtags(combinedResults, fallbackResults.getContent(), addedHashtagIds, TOP_TRENDING_LIMIT);

        return combinedResults.size() > TOP_TRENDING_LIMIT ? combinedResults.subList(0, TOP_TRENDING_LIMIT) : combinedResults;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrendingHashtag> getPaginatedTrending(Pageable pageable) {
        // For the general paginated list, consider hashtags active in a defined recent period (e.g., last 90 days)
        // and order them by their global popularity.
        LocalDateTime since = LocalDateTime.now().minusDays(PAGINATED_POPULAR_RECENCY_DAYS);
        return hashtagRepo.findPopularAndRecentlyActiveHashtags(since, pageable);
    }

    private void addDistinctTrendingHashtags(List<TrendingHashtag> targetList,
                                             List<TrendingHashtag> sourceList,
                                             Set<Long> existingIds,
                                             int limit) {
        for (TrendingHashtag th : sourceList) {
            if (targetList.size() >= limit) {
                break;
            }
            if (th.getHashtag() != null && th.getHashtag().getId() != null) {
                if (existingIds.add(th.getHashtag().getId())) { // Check if ID already added
                    targetList.add(th);
                }
            }
        }
    }
}