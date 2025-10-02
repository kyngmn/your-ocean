package com.myocean.response;

import com.myocean.response.status.ErrorStatus;
import com.myocean.response.status.SuccessStatus;

public record ApiResponse<T>(
        Boolean isSuccess,
        String code,
        String message,
        T result) {

    public static final ApiResponse<Void> OK = new ApiResponse<>(true, SuccessStatus.OK.getCode(),
            SuccessStatus.OK.getMessage(), null);

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),
                result);
    }

    public static <T> ApiResponse<T> onSuccess(SuccessStatus status, T result) {
        return new ApiResponse<>(true, status.getCode(), status.getMessage(), result);
    }

    public static <T> ApiResponse<T> onSuccess(String message, T result) {
        return new ApiResponse<>(true, SuccessStatus.OK.getCode(), message, result);
    }

    public static <T> ApiResponse<T> onFailure(ErrorStatus errorStatus, T data) {
        return new ApiResponse<>(false, errorStatus.getCode(), errorStatus.getMessage(), data);
    }
}
