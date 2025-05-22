package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryJpa extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
    List<User> findByNicknameStartsWithIgnoreCase(String nickname, Pageable pageable);
}
