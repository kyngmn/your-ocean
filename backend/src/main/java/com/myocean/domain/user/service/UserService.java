package com.myocean.domain.user.service;

import com.myocean.domain.friendchat.repository.FriendInvitationRepository;
import com.myocean.domain.gamesession.repository.GameSessionRepository;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.domain.report.repository.ReportRepository;
import com.myocean.domain.survey.repository.SurveyAnswerRepository;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import com.myocean.domain.friendchat.repository.FriendRepository;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.domain.user.converter.UserConverter;
import com.myocean.domain.user.dto.response.UserResponse;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.AiStatus;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.global.service.FileUploadService;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final FileUploadService fileUploadService;
    private final FriendInvitationRepository friendInvitationRepository;
    private final GameSessionRepository gameSessionRepository;
    private final DiaryRepository diaryRepository;
    private final ReportRepository reportRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final Big5ResultRepository big5ResultRepository;
    private final FriendRepository friendRepository;
    private final MyChatRepository myChatRepository;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        return userConverter.toResponse(user);
    }

    public UserResponse updateUserProfile(Integer userId, String nickname, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        // 닉네임 업데이트
        if (nickname != null && !nickname.trim().isEmpty()) {
            String trimmedNickname = nickname.trim();
            validateNickname(trimmedNickname);

            // 닉네임 중복 검사 (기존 사용자 제외)
            if (!user.getNickname().equals(trimmedNickname) &&
                    userRepository.existsByNickname(trimmedNickname)) {
                throw new GeneralException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
            }
            user.setNickname(trimmedNickname);
        }

        // 파일 업데이트
        if (file != null && !file.isEmpty()) {
            if (!fileUploadService.isImageFile(file)) {
                throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
            }

            deleteOldProfileImage(user);

            // 새 프로필 이미지 업로드
            try {
                String profileImageUrl = fileUploadService.uploadFile(file, "profiles");
                user.setProfileImageUrl(profileImageUrl);
            } catch (Exception e) {
                log.error("사용자 프로필 업데이트 실패: {}", userId, e);
                throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED, e);
            }
        }

        User savedUser = userRepository.save(user);
        log.info("사용자 프로필 업데이트 완료 - userId: {}", userId);
        return userConverter.toResponse(savedUser);
    }

    private void deleteOldProfileImage(User user) {
        if (user.getProfileImageUrl() != null) {
            try {
                String objectName = extractObjectNameFromUrl(user.getProfileImageUrl());
                fileUploadService.deleteFile(objectName);
            } catch (Exception e) {
                log.warn("이전 프로필 이미지 삭제 실패: {}", e.getMessage());
            }
        }
    }

    private String extractObjectNameFromUrl(String url) {
        try {
            // URL 형식: http://localhost:9000/myocean-profiles/profiles/user123.jpg
            // 추출 목표: profiles/user123.jpg

            String[] parts = url.split("/");

            // 최소 형식: http://host/bucket/object (parts.length >= 5)
            if (parts.length < 5) {
                throw new IllegalArgumentException("유효하지 않은 MinIO URL format");
            }

            // parts[0] = "http:" or "https:"  // parts[1] = ""
            // parts[2] = "localhost:9000"     // parts[3] = "myocean-profiles" (bucket name)
            // parts[4]~ = "profiles/user123.jpg" (object path)

            StringBuilder objectName = new StringBuilder();
            for (int i = 4; i < parts.length; i++) {
                if (i > 4) objectName.append("/");
                objectName.append(parts[i]);
            }

            return objectName.toString();

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
        }
    }

    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        log.info("사용자 삭제 시작 - userId: {}", userId);

        // 연관 데이터 삭제
        friendInvitationRepository.deleteByInviterUserIdOrInviteeUserId(userId, userId);
        diaryRepository.deleteByUserId(userId);
        reportRepository.deleteByUserId(userId);
        big5ResultRepository.deleteByUserId(userId);
        friendRepository.deleteByUserIdOrFriendId(userId);
        myChatRepository.deleteByUserId(userId);

        // User 삭제 (cascade로 UserPersona, Actor도 삭제)
        userRepository.delete(user);
        log.info("사용자 삭제 완료 - userId: {}", userId);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        String trimmedNickname = nickname == null ? "" : nickname.trim();
        validateNickname(trimmedNickname);
        return !userRepository.existsByNickname(trimmedNickname);
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.NICKNAME_EMPTY);
        }

        String trimmedNickname = nickname.trim();

        if (trimmedNickname.length() < 2 || trimmedNickname.length() > 10) {
            throw new GeneralException(ErrorStatus.NICKNAME_INVALID_FORMAT);
        }

        if (!trimmedNickname.matches("^[가-힣a-zA-Z0-9]{2,10}$")) {
            throw new GeneralException(ErrorStatus.NICKNAME_INVALID_FORMAT);
        }
    }

    public void updateAiStatus(Integer userId, AiStatus aiStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        user.setAiStatus(aiStatus);
        userRepository.save(user);
        log.info("사용자 AI 상태 업데이트 완료 - userId: {}, aiStatus: {}", userId, aiStatus);
    }
}