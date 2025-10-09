package com.myocean.domain.mychat.repository;

import com.myocean.domain.mychat.entity.MyChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyChatRepository extends JpaRepository<MyChatMessage, Long> {

    @Query("SELECT m FROM MyChatMessage m WHERE m.user.id = :userId ORDER BY m.createdAt DESC")
    Page<MyChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MyChatMessage m WHERE m.user.id = :userId")
    Long countByUserId(@Param("userId") Integer userId);

    // 안읽은 메시지 조회 (폴링용)
    @Query("SELECT m FROM MyChatMessage m WHERE m.user.id = :userId AND m.isRead = false ORDER BY m.createdAt ASC")
    List<MyChatMessage> findByUserIdAndIsReadFalseOrderByCreatedAtAsc(@Param("userId") Integer userId);

    // 읽음 처리 (복수 메시지)
    @Modifying
    @Query("UPDATE MyChatMessage m SET m.isRead = true WHERE m.id IN :messageIds")
    void updateIsReadByIds(@Param("messageIds") List<Long> messageIds);

    // 특정 사용자의 안읽은 메시지 개수
    @Query("SELECT COUNT(m) FROM MyChatMessage m WHERE m.user.id = :userId AND m.isRead = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);

    // 성능 최적화: 특정 사용자의 모든 채팅 메시지를 효율적으로 삭제
    void deleteByUser_Id(Integer userId);
}