package com.codedstreams.codedx.sso.sdk.auth;

import com.codedstreams.codedx.sso.sdk.config.SsoConfig;
import com.codedstreams.codedx.sso.sdk.exception.SsoException;
import com.codedstreams.codedx.sso.sdk.model.AuthResponse;
import com.codedstreams.codedx.sso.sdk.model.UserProfile;
import com.codedstreams.codedx.sso.sdk.util.HttpClient;
import com.codedstreams.codedx.sso.sdk.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the SsoAuthenticator interface.
 * <p>
 * This class handles all authentication operations by making HTTP requests
 * to the CodedX SSO service API endpoints.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.5
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SsoAuthenticatorImpl implements SsoAuthenticator {

    private final SsoConfig config;
    private final HttpClient httpClient;

    /**
     * Constructs a new authenticator with the specified configuration.
     *
     * @param config the SSO configuration
     */
    public SsoAuthenticatorImpl(SsoConfig config) {
        this.config = config;
        this.httpClient = new HttpClient(config);
    }

    @Override
    public AuthResponse login(String identifier, String password) throws SsoException {
        validateNotEmpty(identifier, "identifier");
        validateNotEmpty(password, "password");

        try {
            log.debug("Attempting login for identifier: {}", maskIdentifier(identifier));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("identifier", identifier);
            requestBody.put("password", password);
            requestBody.put("clientAppId", config.getClientAppId());

            String response = httpClient.post("/auth/login", requestBody);
            AuthResponse authResponse = JsonUtils.fromJson(response, AuthResponse.class);

            log.info("Login successful for user: {}", maskIdentifier(identifier));
            return authResponse;

        } catch (Exception e) {
            log.error("Login failed for identifier: {}", maskIdentifier(identifier), e);
            throw new SsoException("Login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) throws SsoException {
        validateNotEmpty(refreshToken, "refreshToken");

        try {
            log.debug("Refreshing access token");

            Map<String, Object> params = new HashMap<>();
            params.put("refreshToken", refreshToken);
            params.put("clientAppId", config.getClientAppId());

            String response = httpClient.post("/auth/refresh", params);
            AuthResponse authResponse = JsonUtils.fromJson(response, AuthResponse.class);

            log.info("Token refresh successful");
            return authResponse;

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new SsoException("Token refresh failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void logout(String accessToken) throws SsoException {
        validateNotEmpty(accessToken, "accessToken");

        try {
            log.debug("Logging out user");

            Map<String, Object> params = new HashMap<>();
            params.put("sessionId", extractSessionId(accessToken));

            httpClient.post("/auth/logout", params, accessToken);
            log.info("Logout successful");

        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new SsoException("Logout failed: " + e.getMessage(), e);
        }
    }

    @Override
    public UserProfile getUserProfile(String accessToken) throws SsoException {
        validateNotEmpty(accessToken, "accessToken");

        try {
            log.debug("Retrieving user profile");

            String response = httpClient.get("/user/profile", accessToken);
            UserProfile userProfile = JsonUtils.fromJson(response, UserProfile.class);

            log.debug("User profile retrieved successfully");
            return userProfile;

        } catch (Exception e) {
            log.error("Failed to retrieve user profile", e);
            throw new SsoException("Failed to retrieve user profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateToken(String accessToken) throws SsoException {
        validateNotEmpty(accessToken, "accessToken");

        try {
            log.debug("Validating access token");

            String response = httpClient.get("/auth/validate", accessToken);
            return Boolean.parseBoolean(response.trim());

        } catch (Exception e) {
            log.error("Token validation failed", e);
            throw new SsoException("Token validation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void registerUser(String email, String phoneNumber, String password) throws SsoException {
        if ((email == null || email.trim().isEmpty()) &&
                (phoneNumber == null || phoneNumber.trim().isEmpty())) {
            throw new IllegalArgumentException("Either email or phone number must be provided");
        }
        validateNotEmpty(password, "password");

        try {
            log.debug("Registering new user - Email: {}, Phone: {}",
                    maskEmail(email), maskPhone(phoneNumber));

            Map<String, Object> requestBody = new HashMap<>();
            if (email != null) requestBody.put("email", email.trim());
            if (phoneNumber != null) requestBody.put("phoneNumber", phoneNumber.trim());
            requestBody.put("password", password);
            requestBody.put("clientAppId", config.getClientAppId());

            httpClient.post("/auth/register", requestBody);
            log.info("User registration successful");

        } catch (Exception e) {
            log.error("User registration failed", e);
            throw new SsoException("User registration failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void requestPasswordReset(String identifier, boolean isEmail) throws SsoException {
        validateNotEmpty(identifier, "identifier");

        try {
            log.debug("Requesting password reset for identifier: {}",
                    isEmail ? maskEmail(identifier) : maskPhone(identifier));

            Map<String, Object> params = new HashMap<>();
            params.put("identifier", identifier);
            params.put("isEmail", isEmail);

            httpClient.post("/auth/password/reset/request", params);
            log.info("Password reset request sent successfully");

        } catch (Exception e) {
            log.error("Password reset request failed", e);
            throw new SsoException("Password reset request failed: " + e.getMessage(), e);
        }
    }


    @Override
    public void verifyAccount(String code, String identifier) throws SsoException {
        validateNotEmpty(code, "code");
        validateNotEmpty(identifier, "identifier");

        try {
            log.debug("Verifying account for identifier: {} with code: {}",
                    maskIdentifier(identifier), maskCode(code));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("identifier", identifier);  // Field order matches VerificationRequest
            requestBody.put("code", code);
            requestBody.put("isEmail", identifier.contains("@"));


            httpClient.post("/auth/verify/confirm", requestBody);
            log.info("Account verification successful for: {}", maskIdentifier(identifier));

        } catch (Exception e) {
            log.error("Account verification failed for: {} with code: {}",
                    maskIdentifier(identifier), maskCode(code), e);

            String errorMessage = e.getMessage();
            if (errorMessage.contains("User not found")) {
                throw new SsoException("Account not found. Please check the identifier and try again.", e);
            } else if (errorMessage.contains("Invalid verification code")) {
                throw new SsoException("Invalid verification code. Please check the code and try again.", e);
            } else {
                throw new SsoException("Account verification failed: " + errorMessage, e);
            }
        }
    }

    @Override
    public void resendVerificationCode(String identifier) throws SsoException {
        validateNotEmpty(identifier, "identifier");

        try {
            log.debug("Resending verification code for identifier: {}", maskIdentifier(identifier));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("identifier", identifier);

            try {
                httpClient.post("/auth/verify/resend", requestBody);
                log.info("Verification code resent successfully to: {}", maskIdentifier(identifier));
            } catch (SsoException e) {
                if (e.getMessage().contains("404") || e.getMessage().contains("Not Found")) {
                    throw new SsoException("Resend verification is not currently available. Please try registering again or contact support.", e);
                }
                throw e; // Re-throw other errors
            }

        } catch (Exception e) {
            log.error("Failed to resend verification code to: {}", maskIdentifier(identifier), e);
            throw new SsoException("Failed to resend verification code: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value the string to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if value is null or empty
     */
    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    /**
     * Extracts a session ID from the access token (simplified implementation).
     *
     * @param accessToken the JWT access token
     * @return a simplified session identifier
     */
    private String extractSessionId(String accessToken) {
        // In a real implementation, you might decode the JWT to get the session ID
        // For simplicity, we use the first 20 characters of the token
        return accessToken.length() > 20 ? accessToken.substring(0, 20) : accessToken;
    }

    /**
     * Masks an email address for logging purposes.
     *
     * @param email the email address to mask
     * @return masked email address
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 5) return "***";
        int atIndex = email.indexOf('@');
        if (atIndex > 3) {
            return email.substring(0, 3) + "***" + email.substring(atIndex);
        }
        return "***" + email.substring(Math.max(0, email.length() - 3));
    }

    /**
     * Masks a phone number for logging purposes.
     *
     * @param phone the phone number to mask
     * @return masked phone number
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "***";
        return "***" + phone.substring(Math.max(0, phone.length() - 4));
    }

    /**
     * Masks an identifier (email or phone) for logging.
     *
     * @param identifier the identifier to mask
     * @return masked identifier
     */
    private String maskIdentifier(String identifier) {
        if (identifier == null) return "***";
        return identifier.contains("@") ? maskEmail(identifier) : maskPhone(identifier);
    }

    /**
     * Masks a verification code for logging.
     *
     * @param code the verification code to mask
     * @return masked code
     */
    private String maskCode(String code) {
        if (code == null || code.length() < 2) return "***";
        return code.charAt(0) + "***" + code.charAt(code.length() - 1);
    }
}