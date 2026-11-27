package com.rinconsur.rinconsur_backend.exception;

/** Se traduce a HTTP 409 (duplicados, FK en uso, etc.). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
