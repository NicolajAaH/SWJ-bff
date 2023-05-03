package dk.sdu.mmmi.backendforfrontend;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtTestUtils {

    public static String createMockJwt(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        claims.put("userId", "12345abc");
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000L); // Set expiration time to 1 hour from now
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, "c2VjcmV0X2VuY29kZWRfaW5fYmFzZTY0X3JhbmRvbV9sZXR0ZXJz")
                .compact();
    }

}
