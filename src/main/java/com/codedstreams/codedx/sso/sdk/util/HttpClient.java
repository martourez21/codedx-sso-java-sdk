package com.codedstreams.codedx.sso.sdk.util;

import com.codedstreams.codedx.sso.sdk.config.SsoConfig;
import com.codedstreams.codedx.sso.sdk.exception.SsoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.util.Map;

/**
 * HTTP client for making requests to the CodedX SSO service.
 * <p>
 * This class handles all HTTP communication with the SSO service API,
 * including request building, header management, and response handling.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HttpClient {

    private final SsoConfig config;

    /**
     * Sends a POST request to the specified endpoint.
     *
     * @param endpoint the API endpoint (e.g., "/auth/login")
     * @param body the request body as a map
     * @return the response body as string
     * @throws SsoException if the request fails
     */
    public String post(String endpoint, Map<String, Object> body) throws SsoException {
        return post(endpoint, body, null);
    }

    /**
     * Sends a POST request with authentication.
     *
     * @param endpoint the API endpoint
     * @param body the request body
     * @param authToken the authentication token (optional)
     * @return the response body
     * @throws SsoException if the request fails
     */
    public String post(String endpoint, Map<String, Object> body, String authToken) throws SsoException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpPost httpPost = new HttpPost(config.getBaseUrl() + endpoint);
            setCommonHeaders(httpPost, authToken);

            String jsonBody = JsonUtils.toJson(body);
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            log.debug("Sending POST request to: {}", endpoint);
            return executeRequest(httpClient, httpPost);

        } catch (Exception e) {
            throw new SsoException("HTTP POST request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a GET request to the specified endpoint.
     *
     * @param endpoint the API endpoint
     * @param authToken the authentication token
     * @return the response body
     * @throws SsoException if the request fails
     */
    public String get(String endpoint, String authToken) throws SsoException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpGet httpGet = new HttpGet(config.getBaseUrl() + endpoint);
            setCommonHeaders(httpGet, authToken);

            log.debug("Sending GET request to: {}", endpoint);
            return executeRequest(httpClient, httpGet);

        } catch (Exception e) {
            throw new SsoException("HTTP GET request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Sets common headers for all requests.
     *
     * @param request the HTTP request
     * @param authToken the authentication token
     */
    private void setCommonHeaders(org.apache.hc.client5.http.classic.methods.HttpUriRequest request, String authToken) {
        request.setHeader("X-API-KEY", config.getApiKey());
        request.setHeader("X-API-SECRET", config.getApiSecret());
        request.setHeader("Content-Type", "application/json");
        request.setHeader("User-Agent", config.getUserAgent());

        if (authToken != null) {
            request.setHeader("Authorization", "Bearer " + authToken);
        }
    }

    /**
     * Executes the HTTP request and handles the response.
     *
     * @param httpClient the HTTP client
     * @param request the request to execute
     * @return the response body
     * @throws Exception if the request fails
     */
    private String executeRequest(CloseableHttpClient httpClient,
                                  org.apache.hc.client5.http.classic.methods.HttpUriRequest request) throws Exception {

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode >= 200 && statusCode < 300) {
                log.debug("Request successful - Status: {}", statusCode);
                return responseBody;
            } else {
                log.error("HTTP error - Status: {}, Response: {}", statusCode, responseBody);
                throw new SsoException("HTTP error " + statusCode + ": " + responseBody);
            }
        }
    }

    /**
     * Creates a configured HTTP client.
     *
     * @return the HTTP client instance
     */
    private CloseableHttpClient createHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(config.getConnectTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(config.getReadTimeout()))
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
