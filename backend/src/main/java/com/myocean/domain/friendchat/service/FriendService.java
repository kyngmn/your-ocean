package com.myocean.domain.friendchat.service;

import com.myocean.domain.friendchat.dto.FriendResponse;
import com.myocean.domain.friendchat.entity.Friend;
import com.myocean.domain.friendchat.entity.FriendInvitation;
import com.myocean.domain.friendchat.entity.FriendInvitation.InvitationStatus;
import com.myocean.domain.friendchat.repository.FriendInvitationRepository;
import com.myocean.domain.friendchat.repository.FriendRepository;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.domain.user.repository.UserPersonaRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendInvitationRepository friendInvitationRepository;
    private final UserRepository userRepository;
    private final UserPersonaRepository userPersonaRepository;

    @Transactional(readOnly = true)
    public List<Friend> getFriends(Integer userId) {
        return friendRepository.findActiveByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriendsWithPersonaInfo(List<Friend> friends) {
        if (friends.isEmpty()) {
            return List.of();
        }

        // 친구 ID들 수집
        List<Integer> friendIds = friends.stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toList());

        // 페르소나가 있는 사용자 ID들 조회 (N+1 방지를 위한 배치 조회)
        Set<Integer> userIdsWithPersona = userPersonaRepository.findUserIdsWithPersona(friendIds);

        // FriendResponse 생성
        return friends.stream()
                .map(friend -> FriendResponse.from(
                        friend,
                        userIdsWithPersona.contains(friend.getFriendId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public FriendInvitation createInvitation(Integer inviterId) {
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        FriendInvitation invitation = FriendInvitation.builder()
                .inviterUserId(inviterId)
                .inviteeUserId(null) // 링크를 통해 접속한 사람이 invitee가 됨
                .status(InvitationStatus.PENDING)
                .build();

        return friendInvitationRepository.save(invitation);
    }

    @Transactional
    public Map<String, Object> createInvitationWithUserInfo(Integer inviterId) {
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        FriendInvitation invitation = FriendInvitation.builder()
                .inviterUserId(inviterId)
                .inviteeUserId(null) // 링크를 통해 접속한 사람이 invitee가 됨
                .status(InvitationStatus.PENDING)
                .build();

        FriendInvitation savedInvitation = friendInvitationRepository.save(invitation);

        String inviteLink = "/api/v1/friends/invites/" + savedInvitation.getInvitationToken() + "/accept";

        Map<String, Object> result = new HashMap<>();
        result.put("invitationToken", savedInvitation.getInvitationToken());
        result.put("inviteLink", inviteLink);
        result.put("inviterNickname", inviter.getNickname());
        result.put("inviterProfileImageUrl", inviter.getProfileImageUrl());
        result.put("inviterUserId", inviter.getId());

        return result;
    }

    @Transactional
    public void acceptInvitation(String token, Integer accepterId) {
        FriendInvitation invitation = friendInvitationRepository.findByInvitationToken(token)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVITATION_NOT_FOUND));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new GeneralException(ErrorStatus.INVITATION_ALREADY_PROCESSED);
        }

        if (invitation.getInviterUserId().equals(accepterId)) {
            throw new GeneralException(ErrorStatus.CANNOT_INVITE_SELF);
        }

        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 이미 활성 친구인지 확인
        Optional<Friend> existingActiveFriendship = friendRepository.findActiveFriendship(
                invitation.getInviterUserId(), accepterId);

        if (existingActiveFriendship.isPresent()) {
            throw new GeneralException(ErrorStatus.ALREADY_FRIENDS);
        }

        // 초대 상태 업데이트
        invitation.setInviteeUserId(accepterId);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());

        // 삭제된 친구 관계가 있는지 확인하고 재활성화
        Optional<Friend> deletedFriendship1 = friendRepository.findByUserIdAndFriendIdIncludeDeleted(
                invitation.getInviterUserId(), accepterId);
        Optional<Friend> deletedFriendship2 = friendRepository.findByUserIdAndFriendIdIncludeDeleted(
                accepterId, invitation.getInviterUserId());

        LocalDateTime now = LocalDateTime.now();

        if (deletedFriendship1.isPresent()) {
            // 기존 관계 재활성화 (deleted_at만 null로 변경)
            Friend friend1 = deletedFriendship1.get();
            friend1.restore();
            friendRepository.save(friend1);
        } else {
            // 새로운 관계 생성
            Friend friendship1 = Friend.builder()
                    .userId(invitation.getInviterUserId())
                    .friendId(accepterId)
                    .build();
            friendRepository.save(friendship1);
        }

        if (deletedFriendship2.isPresent()) {
            // 기존 관계 재활성화 (deleted_at만 null로 변경)
            Friend friend2 = deletedFriendship2.get();
            friend2.restore();
            friendRepository.save(friend2);
        } else {
            // 새로운 관계 생성
            Friend friendship2 = Friend.builder()
                    .userId(accepterId)
                    .friendId(invitation.getInviterUserId())
                    .build();
            friendRepository.save(friendship2);
        }
    }

    @Transactional
    public void declineInvitation(String token, Integer declinerId) {
        FriendInvitation invitation = friendInvitationRepository.findByInvitationToken(token)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVITATION_NOT_FOUND));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new GeneralException(ErrorStatus.INVITATION_ALREADY_PROCESSED);
        }

        if (invitation.getInviterUserId().equals(declinerId)) {
            throw new GeneralException(ErrorStatus.CANNOT_INVITE_SELF);
        }

        invitation.setInviteeUserId(declinerId);
        invitation.setStatus(InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
    }

    @Transactional
    public void removeFriend(Integer userId, Integer friendId) {
        // 양방향으로 친구 관계 찾기
        Optional<Friend> friendship1 = friendRepository.findByUserIdAndFriendIdAndDeletedAtIsNull(userId, friendId);
        Optional<Friend> friendship2 = friendRepository.findByUserIdAndFriendIdAndDeletedAtIsNull(friendId, userId);

        if (friendship1.isEmpty() && friendship2.isEmpty()) {
            throw new GeneralException(ErrorStatus.FRIENDSHIP_NOT_FOUND);
        }

        // 소프트 삭제
        friendship1.ifPresent(Friend::delete);
        friendship2.ifPresent(Friend::delete);
    }
}