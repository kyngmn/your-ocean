package com.myocean.domain.user.repository;

import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByProviderAndSocialId(Provider provider, String socialId);

    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);
}