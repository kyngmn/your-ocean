package com.myocean.domain.user.repository;

import com.myocean.domain.user.entity.UserPersona;
import com.myocean.global.enums.BigCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserPersonaRepository extends JpaRepository<UserPersona, Long> {

    List<UserPersona> findByUserIdAndDeletedAtIsNull(Integer userId);

    /**
     * 페르소나를 가진 사용자 ID 목록 조회
     */
    @Query("SELECT DISTINCT up.userId FROM UserPersona up WHERE up.userId IN :userIds AND up.deletedAt IS NULL")
    Set<Integer> findUserIdsWithPersona(@Param("userIds") List<Integer> userIds);

}