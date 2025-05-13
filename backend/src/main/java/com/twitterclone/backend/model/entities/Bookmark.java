package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true) // Include BaseEntity fields
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Bookmarks")
public class Bookmark extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;
}
