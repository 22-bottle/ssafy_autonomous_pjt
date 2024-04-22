package Potless.Backend.global.exception.member;

import Potless.Backend.global.format.response.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateEmailException() {
        this.errorCode = ErrorCode.EMAIL_DUPLICATED;
    }
}
