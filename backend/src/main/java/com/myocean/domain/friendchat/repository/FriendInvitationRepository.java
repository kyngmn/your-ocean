package com.myocean.domain.friendchat.repository;

import com.myocean.domain.friendchat.entity.FriendInvitation;
import com.myocean.domain.friendchat.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendInvitationRepository extends JpaRepository<FriendInvitation, Integer> {

    Optional<FriendInvitation> findByInvitationToken(String invitationToken);

    @Query("SELECT fi FROM FriendInvitation fi WHERE fi.inviterUserId = :inviterId AND fi.inviteeUserId = :inviteeId AND fi.status = :status")
    Optional<FriendInvitation> findByInviterAndInviteeAndStatus(@Param("inviterId") Integer inviterId, @Param("inviteeId") Integer inviteeId, @Param("status") InvitationStatus status);

    void deleteByInviterUserIdOrInviteeUserId(Integer inviterUserId, Integer inviteeUserId);
}