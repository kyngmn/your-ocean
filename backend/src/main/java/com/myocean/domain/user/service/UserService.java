package com.myocean.domain.user.service;

import com.myocean.domain.friendchat.repository.FriendInvitationRepository;
import com.myocean.domain.gamemanagement.repository.GameSessionRepository;
import com.myocean.domain.gamemanagement.repository.GameSessionResultRepository;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.domain.report.repository.ReportRepository;
import com.myocean.domain.survey.repository.SurveyResponseRepository;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import com.myocean.domain.friendchat.repository.FriendRepository;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.domain.user.dto.converter.UserConverter;
import com.myocean.domain.user.dto.request.CreateUserRequest;
import com.myocean.domain.user.dto.request.UpdateUserRequest;
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
    private final GameSessionResultRepository gameSessionResultRepository;
    private final DiaryRepository diaryRepository;
    private final ReportRepository reportRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final Big5ResultRepository big5ResultRepository;
    private final FriendRepository friendRepository;
    private final MyChatRepository myChatRepository;

    public UserResponse createUser(CreateUserRequest request) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GeneralException(ErrorStatus.USER_DUPLICATE_BY_EMAIL);
        }

        // 닉네임 유효성 검사
        validateNickname(request.nickname());

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.nickname().trim())) {
            throw new GeneralException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
        }

        // 소셜 ID 중복 검사
        if (userRepository.findByProviderAndSocialId(request.provider(), request.socialId()).isPresent()) {
            throw new GeneralException(ErrorStatus.USER_DUPLICATE_BY_EMAIL);
        }

        User user = User.builder()
                .email(request.email())
                .provider(request.provider())
                .socialId(request.socialId())
                .nickname(request.nickname().trim())
                .profileImageUrl(request.profileImageUrl())
                .aiStatus(AiStatus.UNSET)
                .build();

        User savedUser = userRepository.save(user);
        return userConverter.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        return userConverter.toResponse(user);
    }

    public UserResponse updateUser(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        // 닉네임 유효성 검사 및 업데이트
        if (request.nickname() != null && !request.nickname().trim().isEmpty()) {
            validateNickname(request.nickname());

            // 닉네임 중복 검사 (기존 사용자 제외)
            if (!user.getNickname().equals(request.nickname().trim()) &&
                userRepository.existsByNickname(request.nickname().trim())) {
                throw new GeneralException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
            }
            user.setNickname(request.nickname().trim());
        }

        // 프로필 이미지 URL 업데이트
        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl());
        }

        User savedUser = userRepository.save(user);
        return userConverter.toResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        try {
            log.info("Starting optimized hard delete for user: {}", userId);

            // 1. 친구 초대 데이터 삭제 (이미 효율적)
            friendInvitationRepository.deleteByInviterUserIdOrInviteeUserId(userId, userId);

            // 2. 게임 세션 관련 데이터 삭제 (성능 최적화)
            gameSessionResultRepository.deleteByUserId(userId);
            gameSessionRepository.deleteByUserId(userId);

            // 3. 일기 데이터 삭제 (성능 최적화)
            diaryRepository.deleteByUserId(userId);

            // 4. 리포트 데이터 삭제 (성능 최적화)
            reportRepository.deleteByUserId(userId);

            // 5. 설문 응답 데이터 삭제 (성능 최적화)
            surveyResponseRepository.deleteByUserId(userId);

            // 6. Big5 결과 데이터 삭제 (성능 최적화)
            big5ResultRepository.deleteByUserId(userId);

            // 7. 친구 관계 데이터 삭제 (성능 최적화)
            friendRepository.deleteByUserIdOrFriendId(userId);

            // 8. 채팅 메시지 데이터 삭제 (성능 최적화)
            myChatRepository.deleteByUserId(userId);

            // 9. 최종 User 삭제 - JPA cascade로 UserPersona, Actor 등도 삭제됨
            userRepository.delete(user);
            log.info("User deleted successfully with optimized queries: {}", userId);
        } catch (Exception e) {
            log.error("Failed to delete user: {}", userId, e);
            throw new GeneralException(ErrorStatus.USER_NOT_EXIST);
        }
    }

    // OAuth 관련 구현
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Integer userId) {
        return getUserById(userId);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        // 1. 닉네임 유효성 검사
        validateNickname(nickname);

        // 2. 중복 확인 - 사용 가능 여부를 boolean으로 반환
        return !userRepository.existsByNickname(nickname.trim());
    }

    // 닉네임 유효성 검사 메서드
    private void validateNickname(String nickname) {
        // 1. null 또는 빈 문자열 검사
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.NICKNAME_EMPTY);
        }

        String trimmedNickname = nickname.trim();

        // 2. 길이 검사 (2-10글자)
        if (trimmedNickname.length() < 2 || trimmedNickname.length() > 10) {
            throw new GeneralException(ErrorStatus.NICKNAME_INVALID_FORMAT);
        }

        // 3. 한글, 영문, 숫자만 허용 (정규표현식)
        if (!trimmedNickname.matches("^[가-힣a-zA-Z0-9]{2,10}$")) {
            throw new GeneralException(ErrorStatus.NICKNAME_INVALID_FORMAT);
        }
    }

    public UserResponse uploadProfileImage(Integer userId, MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

            if (!fileUploadService.isImageFile(file)) {
                throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
            }

            // 기존 프로필 이미지 삭제
            if (user.getProfileImageUrl() != null) {
                try {
                    String objectName = extractObjectNameFromUrl(user.getProfileImageUrl());
                    fileUploadService.deleteFile(objectName);
                } catch (Exception e) {
                    log.warn("Failed to delete old profile image: {}", e.getMessage());
                }
            }

            // 새 프로필 이미지 업로드
            String profileImageUrl = fileUploadService.uploadFile(file, "profiles");
            user.setProfileImageUrl(profileImageUrl);

            User savedUser = userRepository.save(user);
            log.info("Profile image updated for user: {}", userId);

            return userConverter.toResponse(savedUser);
        } catch (Exception e) {
            log.error("Failed to upload profile image for user: {}", userId, e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
        }
    }

    public UserResponse updateUserProfile(Integer userId, String nickname, MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

            // 닉네임 업데이트
            if (nickname != null && !nickname.trim().isEmpty()) {
                // 닉네임 유효성 검사
                validateNickname(nickname);

                // 닉네임 중복 검사 (기존 사용자 제외)
                if (!user.getNickname().equals(nickname.trim()) &&
                    userRepository.existsByNickname(nickname.trim())) {
                    throw new GeneralException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
                }
                user.setNickname(nickname.trim());
            }

            // 파일 업데이트
            if (file != null && !file.isEmpty()) {
                if (!fileUploadService.isImageFile(file)) {
                    throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
                }

                // 기존 프로필 이미지 삭제
                if (user.getProfileImageUrl() != null) {
                    try {
                        String objectName = extractObjectNameFromUrl(user.getProfileImageUrl());
                        fileUploadService.deleteFile(objectName);
                    } catch (Exception e) {
                        log.warn("Failed to delete old profile image: {}", e.getMessage());
                    }
                }

                // 새 프로필 이미지 업로드
                String profileImageUrl = fileUploadService.uploadFile(file, "profiles");
                user.setProfileImageUrl(profileImageUrl);
            }

            User savedUser = userRepository.save(user);
            log.info("User profile updated: {}", userId);

            return userConverter.toResponse(savedUser);
        } catch (GeneralException e) {
            // 이미 구체적인 에러가 있는 경우 그대로 전파
            log.error("User profile update failed - userId: {}, error: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user profile: {}", userId, e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED, e);
        }
    }

    private String extractObjectNameFromUrl(String url) {
        String[] parts = url.split("/");
        if (parts.length >= 2) {
            return parts[parts.length - 2] + "/" + parts[parts.length - 1];
        }
        return parts[parts.length - 1];
    }

    public void updateAiStatus(Integer userId, AiStatus aiStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_EXIST));

        user.setAiStatus(aiStatus);
        userRepository.save(user);
    }
}