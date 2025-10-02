package com.myocean.domain.mychat.repository;

import com.myocean.domain.mychat.entity.MyChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyChatRepository extends JpaRepository<MyChatMessage, Long> {

    @Query("SELECT m FROM MyChatMessage m WHERE m.userId = :userId ORDER BY m.createdAt DESC")
    Page<MyChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT m FROM MyChatMessage m WHERE m.userId = :userId ORDER BY m.createdAt DESC")
    List<MyChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    @Query("SELECT COUNT(m) FROM MyChatMessage m WHERE m.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);

    // 성능 최적화: 특정 사용자의 모든 채팅 메시지를 효율적으로 삭제
    void deleteByUserId(Integer userId);
}