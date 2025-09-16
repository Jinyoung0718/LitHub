package com.sjy.LitHub.global.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {

    // 요청 성공
    SUCCESS(true, 1000, "요청이 성공하였습니다.", HttpStatus.OK),

    // 이메일 인증 관련
    EMAIL_REQUEST_LOCKED(false, 2001, "인증 코드는 30초 후에 다시 요청할 수 있습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED(false, 2002, "이메일 인증이 이미 완료되었습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_EXPIRED(false, 2003, "이메일 인증 시간이 만료되었습니다. 다시 인증을 진행해 주세요.", HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_REQUIRED(false, 2004, "이메일 인증이 필요합니다.", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_CODE(false, 2005, "유효하지 않은 인증 코드입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED(false, 2006, "이메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 회원가입 관련
    USER_ALREADY_EXISTS(false, 2101, "이미 가입된 이메일입니다. 다른 방법으로 로그인하세요.", HttpStatus.CONFLICT),
    USER_PASSWORD_INVALID(false, 2102, "비밀번호는 8~15자 이내로 숫자와 소문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    USER_NICKNAME_DUPLICATE(false, 2103, "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    USER_RECOVERY_REQUIRED(false, 2104, "이 계정은 삭제되었습니다. 로그인 하셔서 계정 복구하시길 바랍니다", HttpStatus.FORBIDDEN),
    USER_LOGIN_RECOVERY_REQUIRED(false, 2111, "이 계정은 삭제되었습니다. 복구하시겠습니까?", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(false, 2105, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TEMP_USER_NOT_FOUND(false, 2106, "임시 사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_NICKNAME_NOT_VERIFIED(false, 2107, "닉네임 중복 검사를 먼저 진행하세요.", HttpStatus.BAD_REQUEST),
    USER_TEMP_SESSION_EXPIRED(false, 2108, "임시 세션이 만료되었습니다. 다시 소셜 로그인을 진행하세요.", HttpStatus.BAD_REQUEST),
    USER_SOCIAL_SIGNUP_REQUIRED(false, 2109, "추가 정보 입력이 필요합니다. 닉네임과 비밀번호를 설정해주세요.", HttpStatus.UNAUTHORIZED),
    USER_PASSWORD_NOT_VALID(false, 2110, "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 로그인 시도 제한 (브루트포스 방어)
    LOGIN_TOO_MANY_ATTEMPTS(false, 2150, "로그인 시도가 너무 많습니다. 잠시 후 다시 시도하세요.", HttpStatus.TOO_MANY_REQUESTS),

    // TEMP_AUTHORIZATION_HEADER 관련
    TEMP_AUTHORIZATION_HEADER_MISSING(false, 2201, "임시 인증 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    TEMP_AUTHORIZATION_HEADER_INVALID(false, 2202, "유효하지 않은 임시 인증 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TEMP_AUTHORIZATION_HEADER_EXPIRED(false, 2203, "임시 인증 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    // OAuth 관련
    OAUTH_USER_NOT_FOUND(false, 2251, "소셜 로그인 사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OAUTH_REDIRECT_FAILED(false, 2252, "소셜 로그인 후 리다이렉트에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 마이페이지 관련
    INVALID_PASSWORD(false, 2301, "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED(false, 2302, "이미 삭제된 계정입니다", HttpStatus.BAD_REQUEST),

    // 친구 관련
    FRIEND_REQUEST_ALREADY_SENT(false, 2401, "이미 친구 요청을 보냈습니다.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_FOUND(false, 2402, "해당 친구 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FRIEND_ALREADY_ACCEPTED(false, 2403, "이미 친구 상태입니다.", HttpStatus.BAD_REQUEST),
    FRIEND_NOT_FOUND(false, 2404, "친구 관계가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // 게시글 관련
    POST_NOT_FOUND(false, 3000, "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(false, 3001, "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TOO_DEEP_COMMENT(false, 3002, "대대댓글은 안 됩니다", HttpStatus.BAD_REQUEST),

    // 실패
    VALIDATION_FAILED(false, 40000, "입력 값이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    AUTH_REQUEST_BODY_INVALID(false, 40001, "잘못된 요청 본문입니다.", HttpStatus.BAD_REQUEST),

    // 인증 & 인가
    UNAUTHORIZED(false, 40002, "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(false, 40003, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    AUTH_CHECK_FAILED(false, 40004, "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    NO_AUTHORITY(false, 40005, "수행할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // JWT 관련
    JWT_BLACKLISTED(false, 40010, "블랙리스트에 등록된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_INVALID(false, 40011, "잘못된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_MISSING(false, 40012, "토큰이 존재하지 않습니다", HttpStatus.UNAUTHORIZED),

    // RefreshToken 관련
    REFRESH_TOKEN_NULL(false, 40020, "리프레시 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(false, 40021, "리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(false, 40022, "유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND(false, 40023, "저장된 리프레시 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),

    // 파일 관련
    INVALID_IMAGE_FORMAT(false, 40100, "허용되지 않은 이미지 확장자입니다.", HttpStatus.BAD_REQUEST),
    EXCEED_MAX_SIZE(false, 40101, "파일 크기가 허용된 최대 크기를 초과하였습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(false, 40102, "유효하지 않은 파일 형식입니다. 이미지 파일만 업로드할 수 있습니다.", HttpStatus.BAD_REQUEST),
    IMAGE_DOWNLOAD_FAILED(false, 40103, "이미지 다운로드 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_DIR_SAVED_FAILED(false, 40104, "이미지 경로 생성 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_PROCESSING_FAILED(false, 40105, "이미지 처리 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_UPLOAD_FAILED(false, 40106, "이미지 업로드 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_DELETE_FAILED(false, 40107, "파일 삭제를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    THUMBNAIL_NOT_FOUND(false, 40108, "썸네일 이미지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PROFILE_IMAGE_NOT_FOUND(false, 40109, "프로필 이미지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNSUPPORTED_VIDEO_UPLOAD(false, 40110, "비디오 파일 업로드는 지원하지 않습니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_AUDIO_UPLOAD(false, 40111, "오디오 파일 업로드는 지원하지 않습니다.", HttpStatus.BAD_REQUEST),

    FILE_UPLOAD_FAILED(false, 40200, "파일 업로드 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_FORMAT(false, 40201, "허용되지 않은 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_EXTENSION(false, 40202, "허용되지 않은 파일 확장자입니다.", HttpStatus.BAD_REQUEST),
    FILE_DOWNLOAD_FAILED(false, 40203, "파일 다운로드 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED(false, 40204, "파일 삭제를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DIR_SAVED_FAILED(false, 40205, "파일 경로 생성 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Redis 관련
    REDIS_UPDATE_TYPE_MISMATCH(false, 40300, "Redis 캐시 업데이트 타입이 일치하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_DESERIALIZATION_FAILED(false, 40301, "Redis 캐시 역직렬화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_CACHE_UPDATE_FAILED(false, 40302, "Redis 캐시 갱신 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Redis 관련 예외 항목 추가
    REDIS_TIMER_NOT_FOUND(false, 40310, "타이머 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    REDIS_TIMER_NOT_OWNER(false, 40311, "타이머를 제어할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    REDIS_TIMER_ALREADY_PAUSED(false, 40312, "이미 일시정지된 타이머입니다.", HttpStatus.BAD_REQUEST),
    REDIS_TIMER_NOT_PAUSED(false, 40313, "타이머가 일시정지 상태가 아닙니다.", HttpStatus.BAD_REQUEST),

    // Follow 관련
    FOLLOW_NOT_FOUND(false, 40400, "해당 팔로우 관계를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_SELF_NOT_ALLOWED(false, 40401, "자기 자신을 팔로우할 수 없습니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_ALREADY_EXISTS(false, 40402, "이미 팔로우 중입니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_DELETE_FAILED(false, 40403, "팔로우 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 그룹 관련
    GROUP_NOT_FOUND(false, 40500, "그룹을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    GROUP_ALREADY_STARTED(false, 40501, "이미 시작된 스터디입니다.", HttpStatus.BAD_REQUEST),
    GROUP_ALREADY_JOINED(false, 40502, "이미 가입된 스터디입니다.", HttpStatus.BAD_REQUEST),
    GROUP_NOT_OWNER(false, 40503, "스터디 그룹의 소유자가 아닙니다.", HttpStatus.FORBIDDEN),
    GROUP_CANCELED(false, 40504, "스터디 그룹이 취소되었습니다.", HttpStatus.BAD_REQUEST);

    private final boolean isSuccess;
    private final int code;
    private final String message;
    private final int httpStatusCode;

    BaseResponseStatus(boolean isSuccess, int code, String message, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatus.value();
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}