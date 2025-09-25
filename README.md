# CodedX SSO SDK

A professional Java SDK for integrating with the CodedX Single Sign-On service. This SDK provides a simple and intuitive API for authentication, user management, and token handling.

## Features

- **User Registration**: Create new user accounts with email/phone verification
- **Account Verification**: Validate user accounts using codes sent via email/SMS
- **User Authentication**: Login with email or phone number
- **Token Management**: Automatic token refresh and validation
- **Profile Management**: Retrieve user information
- **Password Reset**: Request and handle password resets
- **Comprehensive Error Handling**: Detailed exceptions for easy debugging
- **Security**: Built-in security best practices
- **Logging**: Configurable logging for debugging and monitoring

## Installation

### Maven

```xml
<dependency>
    <groupId>com.codedstreams</groupId>
    <artifactId>codedx-sso-java-sdk</artifactId>
    <version>1.0.5</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.codedstreams:codedx-sso-java-sdk:1.0.3'
```

## Quick Start

### 1. Configure the SDK

```java
import com.codedstreams.codedx.sso.sdk.CodedxSsoSdk;
import com.codedstreams.codedx.sso.sdk.config.SsoConfig;
import com.codedstreams.codedx.sso.sdk.auth.SsoAuthenticator;

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

### 2. Complete User Registration and Verification Flow

```java
try {
    // Step 1: Register a new user
    authenticator.registerUser("newuser@example.com", null, "securePassword123");
    System.out.println("Registration successful! Check your email for verification code.");
    
    // Step 2: Verify account using the code sent via email/SMS
    authenticator.verifyAccount("123456", "newuser@example.com");
    System.out.println("Account verified successfully!");
    
    // Step 3: Now the user can login
    AuthResponse response = authenticator.login("newuser@example.com", "securePassword123");
    
    // Use the access token for authenticated requests
    String accessToken = response.getAccessToken();
    String refreshToken = response.getRefreshToken();
    
    // Get user profile
    UserProfile profile = response.getUser();
    System.out.println("Welcome, " + profile.getEmail());
    
} catch (SsoException e) {
    System.err.println("Operation failed: " + e.getMessage());
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

### 4. Resend Verification Code

```java
// If user didn't receive the verification code
authenticator.resendVerificationCode("user@example.com");
System.out.println("Verification code resent successfully!");
```

## Advanced Usage

### Spring Boot Integration

```java
@Configuration
public class SsoConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "codedx.sso")
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
    
    public void registerAndVerifyUser(String email, String phone, String password, String code) {
        try {
            // Register user
            authenticator.registerUser(email, phone, password);
            
            // Verify account using the provided code
            authenticator.verifyAccount(code, email != null ? email : phone);
            
        } catch (SsoException e) {
            throw new RegistrationException("Registration failed: " + e.getMessage(), e);
        }
    }
}
```

### Configuration Properties

```yaml
# application.yaml
codedx:
  sso:
    base-url: https://sso.yourcompany.com/api/sso
    api-key: ${SSO_API_KEY}
    api-secret: ${SSO_API_SECRET}
    client-app-id: ${SSO_CLIENT_APP_ID}
    connect-timeout: 5000
    read-timeout: 10000
    enable-logging: true
```

## Complete Integration Example

### Web Application Integration with Full Registration Flow

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final SsoAuthenticator authenticator;
    
    public AuthController(CodedxSsoSdk sdk) {
        this.authenticator = sdk.getAuthenticator();
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            authenticator.registerUser(request.getEmail(), request.getPhoneNumber(), request.getPassword());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message": "Registration successful. Please check your email for verification code.",
                "nextStep": "verify_account"
            ));
            
        } catch (SsoException e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "message": "Registration failed: " + e.getMessage()
                ));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyAccount(
            @RequestParam String code,
            @RequestParam String identifier) {
        
        try {
            authenticator.verifyAccount(code, identifier);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message": "Account verified successfully! You can now login."
            ));
            
        } catch (SsoException e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "message": "Verification failed: " + e.getMessage()
                ));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerification(@RequestParam String identifier) {
        try {
            authenticator.resendVerificationCode(identifier);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message": "Verification code sent successfully."
            ));
            
        } catch (SsoException e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "message": "Failed to resend verification: " + e.getMessage()
                ));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authenticator.login(request.getIdentifier(), request.getPassword());
            
            return ResponseEntity.ok()
                .body(Map.of(
                    "success", true,
                    "user": response.getUser(),
                    "tokens": Map.of(
                        "accessToken": response.getAccessToken(),
                        "refreshToken": response.getRefreshToken(),
                        "expiresIn": response.getExpiresIn()
                    )
                ));
                
        } catch (SsoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "success", false,
                    "message": "Invalid credentials"
                ));
        }
    }
}
```

### Complete Demo Service Implementation

```java
@Service
@RequiredArgsConstructor
public class SsoService {
    
    private final CodedxSsoSdk ssoSdk;
    private final UserSessionRepository sessionRepository;
    private final EventPublisherService eventPublisher;
    
    public void registerUser(String email, String phoneNumber, String password, String ipAddress) {
        try {
            ssoSdk.getAuthenticator().registerUser(email, phoneNumber, password);
            
            eventPublisher.publishUserEvent("REGISTER",
                "pending", email, ipAddress, null,
                Map.of("registrationTime", LocalDateTime.now())
            );
            
        } catch (SsoException e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
    
    public void verifyAccount(String code, String identifier, String ipAddress) {
        try {
            ssoSdk.getAuthenticator().verifyAccount(code, identifier);
            
            eventPublisher.publishUserEvent("ACCOUNT_VERIFIED",
                "pending", identifier, ipAddress, null,
                Map.of("verificationTime", LocalDateTime.now())
            );
            
        } catch (SsoException e) {
            throw new RuntimeException("Verification failed: " + e.getMessage(), e);
        }
    }
    
    public void resendVerification(String identifier, String ipAddress) {
        try {
            ssoSdk.getAuthenticator().resendVerificationCode(identifier);
            
            eventPublisher.publishUserEvent("VERIFICATION_RESENT",
                "pending", identifier, ipAddress, null,
                Map.of("resendTime", LocalDateTime.now())
            );
            
        } catch (SsoException e) {
            throw new RuntimeException("Failed to resend verification: " + e.getMessage(), e);
        }
    }
    
    public AuthResponse login(String identifier, String password, String ipAddress, String userAgent) {
        try {
            AuthResponse response = ssoSdk.getAuthenticator().login(identifier, password);
            
            // Store session and publish events...
            return response;
            
        } catch (SsoException e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}
```

## API Reference

### SsoAuthenticator Methods

| Method | Description | Parameters |
|--------|-------------|------------|
| `registerUser()` | Create new user account | email, phoneNumber, password |
| `verifyAccount()` | Verify account using code | code, identifier |
| `resendVerificationCode()` | Resend verification code | identifier |
| `login()` | Authenticate user | identifier, password |
| `refreshToken()` | Refresh access token | refreshToken |
| `logout()` | Invalidate session | accessToken |
| `getUserProfile()` | Get user information | accessToken |
| `validateToken()` | Check token validity | accessToken |
| `requestPasswordReset()` | Initiate password reset | identifier, isEmail |

### Complete Registration Flow

1. **User Registration**
   ```java
   authenticator.registerUser("user@example.com", null, "password123");
   ```

2. **System Sends Verification Code** (handled by SSO service)
   - Email: 6-digit code sent to user's email
   - SMS: 6-digit code sent to user's phone

3. **Account Verification**
   ```java
   authenticator.verifyAccount("123456", "user@example.com");
   ```

4. **User Login** (after successful verification)
   ```java
   AuthResponse response = authenticator.login("user@example.com", "password123");
   ```

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

### 1. Complete Registration Flow Implementation

```java
@Service
public class RegistrationService {
    
    public RegistrationResult registerUser(RegistrationRequest request) {
        try {
            // Step 1: Register user
            authenticator.registerUser(request.getEmail(), request.getPhone(), request.getPassword());
            
            // Step 2: Store pending verification (optional)
            verificationService.storePendingVerification(request.getEmail());
            
            return RegistrationResult.success("Verification code sent to your email");
            
        } catch (SsoException e) {
            return RegistrationResult.error("Registration failed: " + e.getMessage());
        }
    }
    
    public VerificationResult verifyAccount(String email, String code) {
        try {
            // Step 3: Verify account
            authenticator.verifyAccount(code, email);
            
            // Step 4: Activate user account
            userService.activateUser(email);
            
            return VerificationResult.success("Account verified successfully");
            
        } catch (SsoException e) {
            return VerificationResult.error("Verification failed: " + e.getMessage());
        }
    }
}
```

### 2. Error Handling for Verification Flow

```java
@ControllerAdvice
public class AuthExceptionHandler {
    
    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ErrorResponse> handleVerificationException(VerificationException e) {
        ErrorResponse error = new ErrorResponse(
            "VERIFICATION_ERROR",
            e.getMessage(),
            Map.of("canRetry", true, "maxAttempts", 3)
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUnverifiedAccount(AccountNotVerifiedException e) {
        ErrorResponse error = new ErrorResponse(
            "ACCOUNT_NOT_VERIFIED",
            "Please verify your account before logging in",
            Map.of("resendUrl", "/api/auth/resend-verification")
        );
        return ResponseEntity.status(403).body(error);
    }
}
```

## Troubleshooting

### Common Registration & Verification Issues

1. **Verification Code Expired**
   ```java
   // Resend a new code
   try {
       authenticator.resendVerificationCode("user@example.com");
   } catch (SsoException e) {
       // Handle resend failure
   }
   ```

2. **Invalid Verification Code**
   ```java
   // Typically allows 3 attempts before locking
   try {
       authenticator.verifyAccount("wrongcode", "user@example.com");
   } catch (SsoException e) {
       if (e.getMessage().contains("invalid code")) {
           // Inform user to try again
       }
   }
   ```

3. **Account Already Verified**
   ```java
   try {
       authenticator.verifyAccount("123456", "user@example.com");
   } catch (SsoException e) {
       if (e.getMessage().contains("already verified")) {
           // Redirect to login page
       }
   }
   ```

### Debug Mode

Enable detailed logging for troubleshooting the complete flow:

```java
SsoConfig config = SsoConfig.builder()
    .enableLogging(true)
    .build();
```

```properties
# Logback configuration for complete flow tracking
<logger name="com.codedstreams.codedx.sso.sdk" level="DEBUG"/>
```

## Support

- **Documentation**: [CodedX SSO Documentation](https://martourez21.github.io/codedx-sso-java-sdk/)
- **API Reference**: Generate with `mvn javadoc:javadoc`
- **Issues**: [GitHub Issues](https://github.com/martourez21/codedx-sso/issues)
- **Contact**: nestorabiawuh@gmail.com

## Versioning

This SDK follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality
- **PATCH** version for bug fixes

## Changelog

### v1.0.4
- Added account verification methods (`verifyAccount`, `resendVerificationCode`)
- Complete registration flow implementation
- Enhanced error handling for verification process
- Improved documentation with complete flow examples

### v1.0.0
- Initial release
- User authentication and registration
- Token management
- Comprehensive error handling
- Spring Boot integration

## License

This SDK is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## Contributing

I welcome contributions! Please just fork and follow along or you can reach out on LinkedIn or just send a mail using my email.

---

**Built with ❤️ by Nestor Martourez**  
[📧 Email](mailto:nestorabiawuh@gmail.com) | [💼 LinkedIn](https://www.linkedin.com/in/nestor-abiangang/) | [🐙 GitHub](https://github.com/martourrez21)