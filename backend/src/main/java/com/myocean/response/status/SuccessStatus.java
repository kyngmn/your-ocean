package com.myocean.response.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus {

    OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    SURVEY_COMPLETE(HttpStatus.OK, "SURVEY201", "설문조사가 완료되었습니다."),

    /*
     Game
     */
    GAME_RESPONSE_SAVED(HttpStatus.CREATED, "GAME2010", "게임 응답이 저장되었습니다."),

    /*
     Chat
     */
    CHAT_MESSAGE_SENT(HttpStatus.CREATED, "CHAT201", "채팅 메시지가 전송되었습니다."),

    /*
     Friend
     */
    FRIEND_INVITATION_SENT(HttpStatus.CREATED, "FRIEND201", "친구 초대가 전송되었습니다."),
    FRIEND_INVITATION_ACCEPTED(HttpStatus.OK, "FRIEND202", "친구 초대를 수락했습니다."),
    FRIEND_INVITATION_REJECTED(HttpStatus.OK, "FRIEND203", "친구 초대를 거절했습니다."),

    /*
     File Upload
     */
    FILE_UPLOAD_SUCCESS(HttpStatus.CREATED, "FILE201", "파일 업로드가 완료되었습니다."),

    /*
     Persona
     */
    PERSONA_EXISTS(HttpStatus.OK, "PERSONA200", "페르소나가 존재합니다."),
    PERSONA_NOT_EXISTS(HttpStatus.OK, "PERSONA404", "페르소나가 생성되지 않았습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
