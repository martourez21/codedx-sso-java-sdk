# CodedX SSO SDK

A professional Java SDK for integrating with the CodedX Single Sign-On service. This SDK provides a simple and intuitive API for authentication, user management, and token handling.

## Features

- **User Authentication**: Login with email or phone number
- **Token Management**: Automatic token refresh and validation
- **User Registration**: Create new user accounts
- **Profile Management**: Retrieve user information
- **Password Reset**: Request and handle password resets
- **Comprehensive Error Handling**: Detailed exceptions for easy debugging
- **Security**: Built-in security best practices
- **Logging**: Configurable logging for debugging and monitoring

## Installation

### Maven

```xml
<dependency>
    <groupId>com.codedx</groupId>
    <artifactId>codedx-sso-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.codedx:codedx-sso-sdk:1.0.0'
```

## Quick Start

### 1. Configure the SDK

```java
import com.codedx.sso.sdk.CodedxSsoSdk;
import com.codedx.sso.sdk.config.SsoConfig;
import com.codedx.sso.sdk.auth.SsoAuthenticator;

SsoConfig config = SsoConfig.builder()
    .baseUrl("https://sso.yourcompany.com/api/sso")
    .apiKey("your-api-key")
    .apiSecret("your-api-secret")
    .clientAppId("your-client-app-id")
    .connectTimeout(5000)
    .readTimeout(10000)
    .enableLogging(true)
    .build();

CodedxSsoSdk sdk = CodedxSsoSdk.create(config);
SsoAuthenticator authenticator = sdk.getAuthenticator();
```

### 2. User Authentication

```java
try {
    // Login with email or phone
    AuthResponse response = authenticator.login("user@example.com", "password");
    
    // Use the access token for authenticated requests
    String accessToken = response.getAccessToken();
    String refreshToken = response.getRefreshToken();
    
    // Get user profile
    UserProfile profile = response.getUser();
    System.out.println("Welcome, " + profile.getEmail());
    
} catch (SsoException e) {
    System.err.println("Authentication failed: " + e.getMessage());
}
```

### 3. Token Management

```java
// Refresh expired token
AuthResponse newTokens = authenticator.refreshToken(refreshToken);

// Validate token
boolean isValid = authenticator.validateToken(accessToken);

// Logout
authenticator.logout(accessToken);
```

### 4. User Registration

```java
// Register with email
authenticator.registerUser("newuser@example.com", null, "securePassword123");

// Register with phone
authenticator.registerUser(null, "+1234567890", "securePassword123");
```

## Advanced Usage

### Spring Boot Integration

```java
@Configuration
public class SsoConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "sso")
    public SsoConfig ssoConfig() {
        return new SsoConfig();
    }
    
    @Bean
    public CodedxSsoSdk ssoSdk(SsoConfig config) {
        return CodedxSsoSdk.create(config);
    }
}

@Service
public class AuthService {
    
    private final SsoAuthenticator authenticator;
    
    public AuthService(CodedxSsoSdk sdk) {
        this.authenticator = sdk.getAuthenticator();
    }
    
    public UserProfile authenticateUser(String identifier, String password) {
        try {
            AuthResponse response = authenticator.login(identifier, password);
            return response.getUser();
        } catch (SsoException e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        }
    }
}
```

### Configuration Properties

```yaml
# application.yaml
sso:
  base-url: https://sso.yourcompany.com/api/sso
  api-key: ${SSO_API_KEY}
  api-secret: ${SSO_API_SECRET}
  client-app-id: ${SSO_CLIENT_APP_ID}
  connect-timeout: 5000
  read-timeout: 10000
  enable-logging: true
```

## Error Handling

The SDK provides comprehensive error handling through specific exception types:

```java
try {
    AuthResponse response = authenticator.login(email, password);
} catch (AuthenticationException e) {
    // Handle authentication-specific errors
    logger.warn("Authentication failed: {}", e.getMessage());
} catch (SsoException e) {
    // Handle general SSO errors
    logger.error("SSO service error: {}", e.getMessage());
} catch (IllegalArgumentException e) {
    // Handle invalid parameters
    logger.error("Invalid parameters: {}", e.getMessage());
}
```

## Complete Integration Example

### Web Application Integration

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final SsoAuthenticator authenticator;
    
    public AuthController(CodedxSsoSdk sdk) {
        this.authenticator = sdk.getAuthenticator();
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authenticator.login(request.getIdentifier(), request.getPassword());
            
            // Set secure HTTP-only cookies
            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", response.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .build();
                
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
            
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Map.of(
                    "success", true,
                    "user", response.getUser()
                ));
                
        } catch (SsoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "success", false,
                    "message", "Invalid credentials"
                ));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            authenticator.registerUser(request.getEmail(), request.getPhoneNumber(), request.getPassword());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Registration successful. Please check your email for verification."
            ));
            
        } catch (SsoException e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "message", "Registration failed: " + e.getMessage()
                ));
        }
    }
}
```

### Mobile App Integration

```java
public class AuthManager {
    private final SsoAuthenticator authenticator;
    private final SecurePreferences securePreferences;
    
    public AuthManager(CodedxSsoSdk sdk, SecurePreferences preferences) {
        this.authenticator = sdk.getAuthenticator();
        this.securePreferences = preferences;
    }
    
    public boolean login(String identifier, String password) {
        try {
            AuthResponse response = authenticator.login(identifier, password);
            
            // Store tokens securely
            securePreferences.storeToken("access_token", response.getAccessToken());
            securePreferences.storeToken("refresh_token", response.getRefreshToken());
            securePreferences.storeUser(response.getUser());
            
            return true;
            
        } catch (SsoException e) {
            Log.e("AuthManager", "Login failed", e);
            return false;
        }
    }
    
    public boolean refreshToken() {
        try {
            String refreshToken = securePreferences.getRefreshToken();
            if (refreshToken == null) return false;
            
            AuthResponse response = authenticator.refreshToken(refreshToken);
            
            // Update stored tokens
            securePreferences.storeToken("access_token", response.getAccessToken());
            securePreferences.storeToken("refresh_token", response.getRefreshToken());
            
            return true;
            
        } catch (SsoException e) {
            Log.e("AuthManager", "Token refresh failed", e);
            return false;
        }
    }
}
```

## API Reference

### SsoAuthenticator Methods

| Method | Description | Parameters |
|--------|-------------|------------|
| `login()` | Authenticate user | identifier, password |
| `refreshToken()` | Refresh access token | refreshToken |
| `logout()` | Invalidate session | accessToken |
| `getUserProfile()` | Get user information | accessToken |
| `validateToken()` | Check token validity | accessToken |
| `registerUser()` | Create new user | email, phoneNumber, password |
| `requestPasswordReset()` | Initiate password reset | identifier, isEmail |

### Response Models

#### AuthResponse
```java
{
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
        "id": "user-123",
        "email": "user@example.com",
        "phoneNumber": "+1234567890",
        "emailVerified": true,
        "phoneVerified": false,
        "createdAt": "2024-01-15T10:00:00",
        "lastLogin": "2024-01-20T15:30:00"
    }
}
```

## Best Practices

### 1. Token Storage
```java
// Web: Use HTTP-only cookies
ResponseCookie tokenCookie = ResponseCookie.from("access_token", token)
    .httpOnly(true)
    .secure(true)
    .sameSite("Strict")
    .build();

// Mobile: Use secure storage
KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
keyStore.load(null);
```

### 2. Error Handling
```java
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SsoException.class)
    public ResponseEntity<ErrorResponse> handleSsoException(SsoException e) {
        ErrorResponse error = new ErrorResponse(
            "AUTHENTICATION_ERROR",
            "Authentication service unavailable",
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
```

### 3. Configuration Management
```java
@Component
public class SsoConfigProvider {
    
    @Value("${sso.base-url}") private String baseUrl;
    @Value("${sso.api-key}") private String apiKey;
    
    @Bean
    public CodedxSsoSdk ssoSdk() {
        // Get secrets from secure source
        String apiSecret = getSecretFromVault("sso-api-secret");
        
        SsoConfig config = SsoConfig.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .clientAppId("my-app")
            .build();
            
        return CodedxSsoSdk.create(config);
    }
}
```

## Troubleshooting

### Common Issues

1. **Connection Timeouts**
   ```java
   SsoConfig config = SsoConfig.builder()
       .connectTimeout(10000)  // Increase timeout
       .readTimeout(30000)
       .build();
   ```

2. **SSL Certificate Issues**
   ```java
   // For development only - disable SSL verification
   System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
   ```

3. **Authentication Failures**
    - Verify API keys are correct
    - Check client application is active
    - Ensure proper scopes are configured

### Debug Mode

Enable detailed logging for troubleshooting:

```java
SsoConfig config = SsoConfig.builder()
    .enableLogging(true)
    .build();
```

```properties
# Logback configuration
<logger name="com.codedstreams.codedx.sso.sdk" level="DEBUG"/>
```

## Support

- **Documentation**: [CodedX SSO Documentation](https://docs.codedx.com/sso)
- **API Reference**: Generate with `mvn javadoc:javadoc`
- **Issues**: [GitHub Issues](https://github.com/martourez21/codedx-sso/issues)
- **Contact**: nestorabiawuh@gmail.com

## Versioning

This SDK follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality
- **PATCH** version for bug fixes

## Changelog

### v1.0.0
- Initial release
- User authentication and registration
- Token management
- Comprehensive error handling
- Spring Boot integration

## License

This SDK is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## Contributing

I welcome contributions! Please just fork and follow along or you can reach out on LinkedIn or mail.

---

**Built with ❤️ by Nestor Martourez**  
[📧 Email](mailto:nestorabiawuh@gmail.com) | [💼 LinkedIn](https://www.linkedin.com/in/nestor-abiangang/) | [🐙 GitHub](https://github.com/martourrez21)