package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Hashtags")
public class Hashtag extends BaseEntity {

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String tag;

    @ManyToMany(mappedBy = "hashtags")
    @Builder.Default
    private List<Tweet> tweets = new ArrayList<>();
}
