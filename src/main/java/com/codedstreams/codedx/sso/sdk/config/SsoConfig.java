package com.codedstreams.codedx.sso.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration class for the CodedX SSO SDK.
 * <p>
 * This class holds all the necessary configuration parameters for connecting
 * to the CodedX SSO service. Use the builder pattern to create instances.
 * </p>
 *
 * <h3>Example:</h3>
 * <pre>{@code
 * SsoConfig config = SsoConfig.builder()
 *     .baseUrl("https://sso.example.com/api/sso")
 *     .apiKey("ak_your_api_key_123")
 *     .apiSecret("as_your_api_secret_456")
 *     .clientAppId("your-client-app-id")
 *     .connectTimeout(5000)
 *     .readTimeout(10000)
 *     .enableLogging(true)
 *     .build();
 * }</pre>
 *
 * @author Nestor Martourez
 * @version 1.0.5
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoConfig {

    /**
     * The base URL of the CodedX SSO service.
     * <p>
     * Example: "https://sso.example.com/api/sso"
     * </p>
     */
    private String baseUrl;

    /**
     * The API key for authenticating with the SSO service.
     * <p>
     * This key is obtained when registering your client application
     * with the CodedX SSO service.
     * </p>
     */
    private String apiKey;

    /**
     * The API secret for authenticating with the SSO service.
     * <p>
     * This secret is obtained along with the API key and should be
     * stored securely.
     * </p>
     */
    private String apiSecret;

    /**
     * The client application ID that identifies your application.
     * <p>
     * This ID is obtained when registering your client application.
     * </p>
     */
    private String clientAppId;

    /**
     * Connection timeout in milliseconds.
     * <p>
     * Default: 5000ms (5 seconds)
     * </p>
     */
    @Builder.Default
    private int connectTimeout = 5000;

    /**
     * Read timeout in milliseconds.
     * <p>
     * Default: 10000ms (10 seconds)
     * </p>
     */
    @Builder.Default
    private int readTimeout = 10000;

    /**
     * Maximum number of retries for failed requests.
     * <p>
     * Default: 3 retries
     * </p>
     */
    @Builder.Default
    private int maxRetries = 3;

    /**
     * Whether to enable debug logging.
     * <p>
     * Default: false
     * </p>
     */
    @Builder.Default
    private boolean enableLogging = false;

    /**
     * User agent string to identify the SDK in requests.
     * <p>
     * Default: "CodedX-SSO-SDK/1.0.0"
     * </p>
     */
    @Builder.Default
    private String userAgent = "CodedX-SSO-SDK/1.0.0";
}