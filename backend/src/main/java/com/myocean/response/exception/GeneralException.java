package com.myocean.response.exception;

import com.myocean.response.status.ErrorStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;
    private final Object data;

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
        this.data = null;
    }

    public GeneralException(ErrorStatus errorStatus, Object data) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
        this.data = data;
    }

    public GeneralException(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
        this.data = null;
    }

    public GeneralException(ErrorStatus errorStatus, Object data, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
        this.data = data;
    }
}
