package com.codedstreams.codedx.sso.sdk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User profile information model.
 * <p>
 * This class represents user profile data returned from the SSO service.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.5
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    /**
     * Unique identifier for the user.
     */
    @JsonProperty("id")
    private String id;

    /**
     * User's email address.
     */
    @JsonProperty("email")
    private String email;

    /**
     * User's phone number.
     */
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    /**
     * Whether the email has been verified.
     */
    @JsonProperty("emailVerified")
    private Boolean emailVerified;

    /**
     * Whether the phone number has been verified.
     */
    @JsonProperty("phoneVerified")
    private Boolean phoneVerified;

    /**
     * When the user account was created.
     */
    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * When the user last logged in.
     */
    @JsonProperty("lastLogin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLogin;
}
