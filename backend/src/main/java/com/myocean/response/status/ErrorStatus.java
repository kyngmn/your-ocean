package com.myocean.response.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {
    /*
    Common
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "로그인 인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    /*
    User
     */
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4000", "입력하신 회원이 존재하지 않습니다."),
    USER_DUPLICATE_BY_EMAIL(HttpStatus.BAD_REQUEST, "USER4001", "이메일이 중복됩니다."),
    USER_NOT_REGISTERED_BY_GOOGLE(HttpStatus.BAD_REQUEST, "USER4002", "신규 유저입니다. 회원가입이 필요합니다."),
    USER_DUPLICATE_BY_NICKNAME(HttpStatus.BAD_REQUEST, "USER4003", "닉네임이 중복됩니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4004", "이미 사용 중인 닉네임입니다."),
    NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "USER4005", "닉네임 형식이 올바르지 않습니다. 2-10글자, 한글/영문/숫자만 가능합니다."),
    NICKNAME_EMPTY(HttpStatus.BAD_REQUEST, "USER4006", "닉네임을 입력해주세요."),
    _NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "COMMON501", "아직 구현되지 않은 기능입니다."),
    /*
    Auth
     */
    INVALID_ACCESSTOKEN(HttpStatus.UNAUTHORIZED, "AUTH4010", "유효하지 않은 accessToken입니다."),
    GOOGLE_USERINFO_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH4011", "Google 사용자 정보를 가져오는 데 실패했습니다."),
    GOOGLE_TOKEN_RESPONSE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH4012", "Google 토큰 응답이 없습니다."),
    GOOGLE_ACCESS_TOKEN_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH4013", "Google accessToken이 비어 있습니다."),
    GOOGLE_USERINFO_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH4014", "Google 사용자 정보 응답이 비어 있습니다."),
    GOOGLE_USERINFO_INCOMPLETE(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH4015", "Google 사용자 정보가 불완전합니다."),
    INVALID_REFRESHTOKEN(HttpStatus.FORBIDDEN, "AUTH4030", "유효하지 않은 refreshToken입니다."),

    // 인증/인가 상세 에러
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH4016", "토큰이 만료되었습니다. 다시 로그인해주세요."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH4017", "유효하지 않은 토큰입니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH4018", "로그인이 필요한 서비스입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH4031", "해당 리소스에 접근할 권한이 없습니다."),
    INSUFFICIENT_PRIVILEGES(HttpStatus.FORBIDDEN, "AUTH4032", "권한이 부족합니다."),

    /*
    Game Session
     */
    GAME_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME4000", "게임 세션을 찾을 수 없습니다."),
    GAME_SESSION_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME4001", "게임 세션 결과를 찾을 수 없습니다."),
    GAME_SESSION_ALREADY_FINISHED(HttpStatus.BAD_REQUEST, "GAME4002", "이미 종료된 게임 세션입니다."),

    /*
    BART Game
     */
    BART_ROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "BART4000", "BART 게임 라운드를 찾을 수 없습니다."),
    BART_ROUND_ALREADY_FINISHED(HttpStatus.BAD_REQUEST, "BART4001", "이미 종료된 BART 게임 라운드입니다."),
    BART_ROUND_CREATION_DATA_MISSING(HttpStatus.BAD_REQUEST, "BART4002", "새 라운드 생성을 위한 데이터가 부족합니다. (색상 및 터지는 지점 필요)"),
    BART_CLICK_INDEX_DUPLICATE(HttpStatus.BAD_REQUEST, "BART4003", "이미 존재하는 클릭 인덱스입니다."),
    BART_BALLOON_ALREADY_POPPED(HttpStatus.BAD_REQUEST, "BART4004", "풍선이 이미 터져서 더 이상 클릭할 수 없습니다."),
    BART_LOGIC_INCONSISTENT(HttpStatus.BAD_REQUEST, "BART4005", "클릭 수와 풍선 터짐 상태가 일치하지 않습니다."),

    /*
    UG Game
     */
    UG_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "UG4000", "UG 게임 오더를 찾을 수 없습니다."),
    /*
    Report
     */
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4040", "요청한 리포트를 찾을 수 없습니다."),

    /*
    Diary
     */
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY4040", "요청한 다이어리를 찾을 수 없습니다."),
    INVALID_YEAR_MONTH_FORMAT(HttpStatus.BAD_REQUEST, "DIARY4001", "올바르지 않은 년월 형식입니다."),

    /*
    Survey
     */
    SURVEY_RESPONSE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SURVEY5000", "설문 응답 저장에 실패했습니다."),
    SURVEY_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SURVEY5001", "빅파이브 점수 계산에 실패했습니다."),

    /*
    GNG Game
     */
    GNG_RESPONSE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "GNG6000", "GNG 게임 응답 저장에 실패했습니다."),
    GNG_INVALID_ROUND_INDEX(HttpStatus.BAD_REQUEST, "GNG6001", "유효하지 않은 라운드 번호입니다."),
    GNG_SESSION_NOT_GNG_GAME(HttpStatus.BAD_REQUEST, "GNG6002", "해당 세션은 GNG 게임 세션이 아닙니다."),
    GNG_RESPONSES_NOT_FOUND(HttpStatus.NOT_FOUND, "GNG6003", "해당 세션의 GNG 응답 데이터를 찾을 수 없습니다."),
    GNG_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "GNG6004", "GNG 게임 결과 계산에 실패했습니다."),
    GNG_RESULT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "GNG6005", "GNG 게임 결과 저장에 실패했습니다."),
    GNG_DUPLICATE_ROUND(HttpStatus.BAD_REQUEST, "GNG6006", "이미 해당 라운드의 응답이 존재합니다."),

    /*
    Friend
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND4040", "사용자를 찾을 수 없습니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND4041", "초대를 찾을 수 없습니다."),
    INVITATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "FRIEND4001", "이미 처리된 초대입니다."),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "FRIEND4002", "자신을 초대할 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "FRIEND4003", "이미 친구 관계입니다."),
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND4042", "친구 관계를 찾을 수 없습니다."),

    /*
    Big5
     */
    BIG5_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "BIG5_4040", "Big5 결과를 찾을 수 없습니다."),
    BIG5_RESULT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "BIG5_4001", "해당 소스에 대한 Big5 결과가 이미 존재합니다."),
    BIG5_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BIG5_5000", "Big5 점수 계산에 실패했습니다."),
    BIG5_INSUFFICIENT_DATA(HttpStatus.BAD_REQUEST, "BIG5_4002", "Big5 계산을 위한 데이터가 부족합니다."),
    BIG5_RESULT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BIG5_5001", "Big5 결과 저장에 실패했습니다."),

    /*
    File Upload
     */
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE4001", "지원되지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE5001", "파일 업로드에 실패했습니다."),

    /*
    Chat
     */
    CHAT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT4030", "해당 채팅방에 접근할 권한이 없습니다."),
    CHAT_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT5000", "채팅 메시지 전송 중 오류가 발생했습니다."),
    DIARY_CHAT_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT5001", "다이어리 채팅 메시지 전송 중 오류가 발생했습니다."),

    /*
    Survey
     */
    SURVEY_INVALID_RESPONSE_COUNT(HttpStatus.BAD_REQUEST, "SURVEY4001", "설문 응답 개수가 올바르지 않습니다."),
    SURVEY_REPORT_JSON_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SURVEY5002", "리포트 JSON 직렬화에 실패했습니다."),

    /*
    OpenAI
     */
    OPENAI_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI5000", "OpenAI API 호출에 실패했습니다."),
    OPENAI_RESPONSE_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI5001", "OpenAI API 응답이 비어있습니다."),
    OPENAI_JSON_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI5002", "OpenAI 응답 JSON 파싱에 실패했습니다."),

    /*
    General
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다.");

    /*
    Enums
     */

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
