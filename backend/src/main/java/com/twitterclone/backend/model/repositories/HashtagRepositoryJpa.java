package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.TrendingHashtag;
import com.twitterclone.backend.model.entities.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HashtagRepositoryJpa extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByTag(String tag);

    List<Hashtag> findByTagStartsWithIgnoreCase(String tag, Pageable pageable);

    /**
     * Finds hashtags that were active (i.e., associated with tweets) since a given time.
     * The resulting TrendingHashtag objects will contain the *global* tweet count for each hashtag.
     * The list is ordered by the number of *distinct recent tweets* each hashtag was associated with,
     * effectively ordering by recent trendiness.
     */
    @Query("""
            SELECT new com.twitterclone.backend.model.TrendingHashtag(
                h,
                (SELECT COUNT(t_global.id) FROM Tweet t_global JOIN t_global.hashtags h_global WHERE h_global.id = h.id)
            )
            FROM Hashtag h
            JOIN h.tweets t_recent
            WHERE t_recent.createdAt >= :since
            GROUP BY h.id, h.tag, h.createdAt, h.updatedAt
            ORDER BY COUNT(DISTINCT t_recent.id) DESC, h.tag ASC
            """)
    List<TrendingHashtag> findTrendingHashtagsAndCountSince(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Finds hashtags that have been active since a given :since datetime,
     * orders them by their *total global tweet count* (descending),
     * and includes this global count in the TrendingHashtag object.
     * This is for a paginated list of generally popular but still relevant hashtags.
     */
    @Query(value = """
            SELECT new com.twitterclone.backend.model.TrendingHashtag(
                h,
                (SELECT COUNT(t_all.id) FROM Tweet t_all JOIN t_all.hashtags h_all WHERE h_all.id = h.id)
            )
            FROM Hashtag h
            JOIN h.tweets t_recent_filter
            WHERE t_recent_filter.createdAt >= :since
            GROUP BY h.id, h.tag, h.createdAt, h.updatedAt
            ORDER BY (SELECT COUNT(t_all2.id) FROM Tweet t_all2 JOIN t_all2.hashtags h_all2 WHERE h_all2.id = h.id) DESC, h.tag ASC
            """)
    Page<TrendingHashtag> findPopularAndRecentlyActiveHashtags(@Param("since") LocalDateTime since, Pageable pageable);
}