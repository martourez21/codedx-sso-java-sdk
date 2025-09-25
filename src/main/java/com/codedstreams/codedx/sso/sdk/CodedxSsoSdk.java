package com.codedstreams.codedx.sso.sdk;

import com.codedstreams.codedx.sso.sdk.auth.SsoAuthenticator;
import com.codedstreams.codedx.sso.sdk.auth.SsoAuthenticatorImpl;
import com.codedstreams.codedx.sso.sdk.config.SsoConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the CodedX SSO SDK.
 * <p>
 * This class provides a factory method to create SDK instances configured
 * for interacting with the CodedX Single Sign-On service.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * SsoConfig config = SsoConfig.builder()
 *     .baseUrl("https://sso.example.com/api/sso")
 *     .apiKey("your-api-key")
 *     .apiSecret("your-api-secret")
 *     .clientAppId("your-client-app-id")
 *     .build();
 *
 * CodedxSsoSdk sdk = CodedxSsoSdk.create(config);
 * SsoAuthenticator authenticator = sdk.getAuthenticator();
 * }</pre>
 *
 * @author Nestor Martourez
 * @version 1.0.5
 * @since 1.0.0
 */
@Slf4j
public class CodedxSsoSdk {

    private final SsoConfig config;

    /**
     * Constructs a new SDK instance with the specified configuration.
     *
     * @param config the SSO configuration
     * @throws IllegalArgumentException if config is null or invalid
     */
    private CodedxSsoSdk(SsoConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SsoConfig cannot be null");
        }
        this.config = config;
        validateConfig();
        log.debug("CodedX SSO SDK initialized for base URL: {}", config.getBaseUrl());
    }

    /**
     * Creates a new SDK instance with the specified configuration.
     *
     * @param config the SSO configuration
     * @return a new CodedxSsoSdk instance
     * @throws IllegalArgumentException if config is null or invalid
     */
    public static CodedxSsoSdk create(SsoConfig config) {
        return new CodedxSsoSdk(config);
    }

    /**
     * Returns the authenticator instance for performing authentication operations.
     *
     * @return SsoAuthenticator instance
     */
    public SsoAuthenticator getAuthenticator() {
        return new SsoAuthenticatorImpl(config);
    }

    /**
     * Returns the configuration used by this SDK instance.
     *
     * @return the SsoConfig instance
     */
    public SsoConfig getConfig() {
        return config;
    }

    /**
     * Validates the configuration parameters.
     *
     * @throws IllegalArgumentException if any required parameter is missing or invalid
     */
    private void validateConfig() {
        if (config.getBaseUrl() == null || config.getBaseUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL is required");
        }
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            throw new IllegalArgumentException("API key is required");
        }
        if (config.getApiSecret() == null || config.getApiSecret().trim().isEmpty()) {
            throw new IllegalArgumentException("API secret is required");
        }
        if (config.getClientAppId() == null || config.getClientAppId().trim().isEmpty()) {
            throw new IllegalArgumentException("Client application ID is required");
        }

        // Validate URL format
        if (!config.getBaseUrl().startsWith("http")) {
            throw new IllegalArgumentException("Base URL must start with http:// or https://");
        }
    }
}