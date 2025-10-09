package com.myocean.domain.mychat.repository;

import com.myocean.domain.mychat.entity.MyChatMessage;
import com.myocean.global.enums.AnalysisStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyChatRepository extends JpaRepository<MyChatMessage, Long> {

    @Query("SELECT m FROM MyChatMessage m WHERE m.user.id = :userId ORDER BY m.createdAt DESC")
    Page<MyChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MyChatMessage m WHERE m.user.id = :userId")
    Long countByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE MyChatMessage m SET m.analysisStatus = :status WHERE m.id = :messageId")
    void updateAnalysisStatus(@Param("messageId") Long messageId, @Param("status") AnalysisStatus status);
}