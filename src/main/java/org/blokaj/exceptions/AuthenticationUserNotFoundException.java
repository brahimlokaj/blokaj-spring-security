package org.blokaj.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationUserNotFoundException extends AuthenticationException {

    public AuthenticationUserNotFoundException(String message) {
        super(message);
    }
}
