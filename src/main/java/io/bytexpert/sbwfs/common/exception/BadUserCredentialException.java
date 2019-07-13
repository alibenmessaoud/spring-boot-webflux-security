package io.bytexpert.sbwfs.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class BadUserCredentialException extends BadCredentialsException {
    public BadUserCredentialException(String message) {
        super(message);
    }
}