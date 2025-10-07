package com.myocean.domain.user.repository;

import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.enums.ActorKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {

    @Query("SELECT a FROM Actor a WHERE a.kind = :kind AND a.userId = :userId")
    Optional<Actor> findByKindAndUserId(@Param("kind") ActorKind kind, @Param("userId") Integer userId);

}