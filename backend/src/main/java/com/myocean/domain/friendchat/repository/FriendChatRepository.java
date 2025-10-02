package com.myocean.domain.friendchat.repository;

import com.myocean.domain.friendchat.entity.FriendChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendChatRepository extends JpaRepository<FriendChatMessage, Integer> {

    @Query("SELECT f FROM FriendChatMessage f WHERE f.roomId = :roomId ORDER BY f.createdAt ASC")
    Page<FriendChatMessage> findByRoomIdOrderByCreatedAtAsc(@Param("roomId") Integer roomId, Pageable pageable);

    @Query("SELECT f FROM FriendChatMessage f WHERE f.roomId = :roomId ORDER BY f.createdAt ASC")
    List<FriendChatMessage> findByRoomIdOrderByCreatedAtAsc(@Param("roomId") Integer roomId);

    @Query("SELECT COUNT(f) FROM FriendChatMessage f WHERE f.roomId = :roomId")
    Long countByRoomId(@Param("roomId") Integer roomId);

    @Query("SELECT f FROM FriendChatMessage f " +
           "JOIN Friend friend ON f.roomId = friend.id " + 
           "WHERE (friend.userId = :userId OR friend.friendId = :userId) " +
           "ORDER BY f.createdAt DESC")
    Page<FriendChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
}