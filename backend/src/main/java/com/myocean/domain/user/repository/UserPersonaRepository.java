package com.myocean.domain.user.repository;

import com.myocean.domain.user.entity.UserPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserPersonaRepository extends JpaRepository<UserPersona, Integer> {

    List<UserPersona> findByUserIdAndDeletedAtIsNull(Integer userId);

    @Query("SELECT DISTINCT up.userId FROM UserPersona up WHERE up.userId IN :userIds AND up.deletedAt IS NULL")
    Set<Integer> findUserIdsWithPersona(@Param("userIds") List<Integer> userIds);

}