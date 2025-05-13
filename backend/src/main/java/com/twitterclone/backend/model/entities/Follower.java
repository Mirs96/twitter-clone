package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true) // Include BaseEntity fields
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Followers")
public class Follower extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
