```java
@PostMapping("users/signin")
public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request) {

    try {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        if (authentication.isAuthenticated()) {

            UserPrincipal userPrincipal =
                    (UserPrincipal) authentication.getPrincipal();

            User user = userPrincipal.getUser();

            String token =
                    jwtService.generateToken(user);

            return ResponseEntity.ok(
                    new AuthResponse(
                            true,
                            "Login successful",
                            token,
                            user.getEmail()
                    )
            );
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(
                        false,
                        "Authentication failed",
                        null,
                        null
                ));

    } catch (BadCredentialsException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(
                        false,
                        "Invalid username or password",
                        null,
                        null
                ));
    }
}
```

You will need these imports:

```java
import com.src.userservice.model.User;
import com.src.userservice.model.UserPrincipal;
import org.springframework.security.core.Authentication;
```

### 2. Add `getUser()` to `UserPrincipal`

Your current class has:

```java
private final User user;

public UserPrincipal(User user) {
    this.user = user;
}
```

Add:

```java
public User getUser() {
    return user;
}
```

So the relevant part becomes:

```java
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // remaining UserDetails methods...
}
```

### Why this is better

Your authentication flow now becomes:

```text
LoginRequest
    |
    | email + password
    ↓
AuthenticationManager
    |
    ↓
MyUserDetailsService
    |
    ↓
UserPrincipal
    |
    | contains User
    ↓
User
 ├── email = testadmin@in.com
 └── role  = ADMIN
    |
    ↓
JwtService
    |
    ↓
JWT
```

The JWT will now contain:

```json
{
  "sub": "testadmin@in.com",
  "role": "ADMIN",
  "iat": 1788853980,
  "exp": 1788855780
}
```

This is exactly what your **Category Service** needs to authorize:

```java
.hasRole("ADMIN")
```

### One thing to verify

Your `MyUserDetailsService` must be returning:

```java
return new UserPrincipal(user);
```

which, based on the code you've shown earlier, it already does.

So you don't need another `UserDetailsService`, and you don't need to query the database again in the login controller.
