package builder;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class JwtBuilder {
    public static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    public Jwt buildDefault() {
        return new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", TEST_USER_ID.toString())
        );
    }
}
