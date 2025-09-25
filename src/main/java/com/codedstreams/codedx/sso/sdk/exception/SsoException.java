package com.codedstreams.codedx.sso.sdk.exception;

/**
 * Exception thrown when SSO operations fail.
 * <p>
 * This exception encapsulates errors that occur during communication with
 * the CodedX SSO service, including authentication failures, network issues,
 * and server errors.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.5
 * @since 1.0.0
 */
public class SsoException extends Exception {

    /**
     * Constructs a new SsoException with the specified detail message.
     *
     * @param message the detail message
     */
    public SsoException(String message) {
        super(message);
    }

    /**
     * Constructs a new SsoException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SsoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SsoException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public SsoException(Throwable cause) {
        super(cause);
    }
}
