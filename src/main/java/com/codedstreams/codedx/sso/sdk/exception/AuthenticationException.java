package com.codedstreams.codedx.sso.sdk.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 * <p>
 * This exception is typically thrown when login attempts fail due to
 * incorrect username, password, or when the account is locked/disabled.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.5
 * @since 1.0.0
 */
public class AuthenticationException extends SsoException {

    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
