package com.myocean.domain.friendchat.repository;

import com.myocean.domain.friendchat.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.deletedAt IS NULL")
    List<Friend> findActiveByUserId(@Param("userId") Integer userId);

    @Query("SELECT f FROM Friend f WHERE (f.userId = :userId OR f.friendId = :userId) AND f.deletedAt IS NULL")
    List<Friend> findByUserIdOrFriendIdAndNotDeleted(@Param("userId") Integer userId);

    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.friendId = :friendId AND f.deletedAt IS NULL")
    Optional<Friend> findByUserIdAndFriendIdAndDeletedAtIsNull(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.friendId = :friendId AND f.deletedAt IS NULL")
    Optional<Friend> findByUserIdAndFriendIdAndNotDeleted(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Query("SELECT f FROM Friend f WHERE ((f.userId = :userId AND f.friendId = :friendId) OR (f.userId = :friendId AND f.friendId = :userId)) AND f.deletedAt IS NULL")
    Optional<Friend> findActiveFriendship(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Query("SELECT f FROM Friend f WHERE ((f.userId = :userId AND f.friendId = :friendId) OR (f.userId = :friendId AND f.friendId = :userId)) AND f.deletedAt IS NULL")
    Optional<Friend> findByUserPairAndNotDeleted(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Query("SELECT COUNT(f) FROM Friend f WHERE (f.userId = :userId OR f.friendId = :userId) AND f.deletedAt IS NULL")
    Long countByUserIdAndNotDeleted(@Param("userId") Integer userId);

    // 삭제된 친구 관계도 포함해서 찾기
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.friendId = :friendId")
    Optional<Friend> findByUserIdAndFriendIdIncludeDeleted(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    // 성능 최적화: 특정 사용자의 모든 친구 관계를 효율적으로 삭제
    @Modifying
    @Query("DELETE FROM Friend f WHERE f.userId = :userId OR f.friendId = :userId")
    void deleteByUserIdOrFriendId(@Param("userId") Integer userId);
}