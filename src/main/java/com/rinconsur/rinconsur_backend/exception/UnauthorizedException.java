package com.rinconsur.rinconsur_backend.exception;

/** Se traduce a HTTP 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}
