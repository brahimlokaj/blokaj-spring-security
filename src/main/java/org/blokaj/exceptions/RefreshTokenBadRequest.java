package org.blokaj.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RefreshTokenBadRequest extends RuntimeException {

    public RefreshTokenBadRequest(String message) {
        super(message);
    }
}
