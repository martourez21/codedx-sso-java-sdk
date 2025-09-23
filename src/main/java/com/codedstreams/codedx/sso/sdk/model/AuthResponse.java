package com.codedstreams.codedx.sso.sdk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response model for authentication operations.
 * <p>
 * This class represents the response received from authentication endpoints
 * containing access tokens, refresh tokens, and user information.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {

    /**
     * The JWT access token for authenticated requests.
     */
    @JsonProperty("accessToken")
    private String accessToken;

    /**
     * The refresh token for obtaining new access tokens.
     */
    @JsonProperty("refreshToken")
    private String refreshToken;

    /**
     * The type of token (usually "Bearer").
     */
    @JsonProperty("tokenType")
    private String tokenType;

    /**
     * The expiration time of the access token in seconds.
     */
    @JsonProperty("expiresIn")
    private Long expiresIn;

    /**
     * The user profile information.
     */
    @JsonProperty("user")
    private UserProfile user;
}