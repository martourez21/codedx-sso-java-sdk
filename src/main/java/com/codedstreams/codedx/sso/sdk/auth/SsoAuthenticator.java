package com.codedstreams.codedx.sso.sdk.auth;

import com.codedstreams.codedx.sso.sdk.exception.SsoException;
import com.codedstreams.codedx.sso.sdk.model.AuthResponse;
import com.codedstreams.codedx.sso.sdk.model.UserProfile;

/**
 * Interface for performing authentication operations with the CodedX SSO service.
 * <p>
 * This interface provides methods for user authentication, token management,
 * and user profile retrieval.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SsoAuthenticator {

    /**
     * Authenticates a user with the provided credentials.
     * <p>
     * The identifier can be either an email address or a phone number.
     * </p>
     *
     * @param identifier the user's email or phone number
     * @param password the user's password
     * @return AuthResponse containing tokens and user information
     * @throws SsoException if authentication fails
     * @throws IllegalArgumentException if identifier or password is null/empty
     */
    AuthResponse login(String identifier, String password) throws SsoException;

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param refreshToken the refresh token obtained during login
     * @return AuthResponse with new access and refresh tokens
     * @throws SsoException if token refresh fails
     * @throws IllegalArgumentException if refreshToken is null/empty
     */
    AuthResponse refreshToken(String refreshToken) throws SsoException;

    /**
     * Invalidates the current user session (logs out the user).
     *
     * @param accessToken the current access token
     * @throws SsoException if logout fails
     * @throws IllegalArgumentException if accessToken is null/empty
     */
    void logout(String accessToken) throws SsoException;

    /**
     * Retrieves the user profile information for the authenticated user.
     *
     * @param accessToken the valid access token
     * @return UserProfile containing user information
     * @throws SsoException if profile retrieval fails
     * @throws IllegalArgumentException if accessToken is null/empty
     */
    UserProfile getUserProfile(String accessToken) throws SsoException;

    /**
     * Validates whether an access token is still valid and not expired.
     *
     * @param accessToken the access token to validate
     * @return true if the token is valid, false otherwise
     * @throws SsoException if validation fails due to network or server error
     * @throws IllegalArgumentException if accessToken is null/empty
     */
    boolean validateToken(String accessToken) throws SsoException;

    /**
     * Registers a new user with the SSO service.
     * <p>
     * At least one of email or phone number must be provided.
     * </p>
     *
     * @param email the user's email address (optional)
     * @param phoneNumber the user's phone number (optional)
     * @param password the user's password
     * @throws SsoException if registration fails
     * @throws IllegalArgumentException if both email and phoneNumber are null/empty
     */
    void registerUser(String email, String phoneNumber, String password) throws SsoException;

    /**
     * Requests a password reset for the specified identifier (email or phone).
     *
     * @param identifier the user's email or phone number
     * @param isEmail true if identifier is an email, false if phone number
     * @throws SsoException if password reset request fails
     * @throws IllegalArgumentException if identifier is null/empty
     */
    void requestPasswordReset(String identifier, boolean isEmail) throws SsoException;

    /**
     * Verifies a user's account using the code sent via email or SMS.
     *
     * @param code the verification code received by the user
     * @param identifier the user's email or phone number
     * @throws SsoException if verification fails
     * @throws IllegalArgumentException if code or identifier is null/empty
     */
    void verifyAccount(String code, String identifier) throws SsoException;

    /**
     * Resends the verification code to the user's email or phone.
     *
     * @param identifier the user's email or phone number
     * @throws SsoException if resend operation fails
     * @throws IllegalArgumentException if identifier is null/empty
     */
    void resendVerificationCode(String identifier) throws SsoException;
}