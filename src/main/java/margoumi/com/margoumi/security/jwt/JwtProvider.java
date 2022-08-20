package margoumi.com.margoumi.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import margoumi.com.margoumi.security.UserPrincipal;
import margoumi.com.margoumi.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtProvider implements IJwtProvider{

    @Value("${app.jwt.secret}")
    private String JWT_SECRET;

    @Value("${app.jwt.expiration-in-ms}")
    private int JWT_EXPIRATION_IN_MS;

    @Override
    public String generateToken(UserPrincipal auth){
         String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining());
         return Jwts.builder().setSubject(auth.getEmail()).claim("roles", authorities)
                 .claim("userId", auth.getId())
                 .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MS))
                 .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                 .compact();
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response){
        Claims claims = extractClaims(request, response);
        if(claims == null) {
            return null;
        }
        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        Set<GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                .map(SecurityUtils::convertToAuthority)
                .collect(Collectors.toSet());

        UserDetails userDetails = UserPrincipal.builder().email(username).authoroties(authorities).id(userId).build();

        if (username ==null){
            return null;
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @Override
    public boolean validateToken(HttpServletRequest request, HttpServletResponse response){
        Claims claims = extractClaims(request, response);
        if(claims == null) {
            return false;
        }
        if(claims.getExpiration().before(new Date())){
            return false;
        }
        return true;
    }

    private Claims extractClaims(HttpServletRequest request, HttpServletResponse response){
        String token = SecurityUtils.extractAuthTokenFromRequest(request);
        if (token == null) {
            return null;
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();

    }
    public String getEmail(String token) {

        Claims claims = getClaims(token);

        if(claims != null) {
            return claims.getSubject();
        }
        return null;
    }
    private Claims getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();

        } catch (Exception e) {
            return null;
        }
    }
    public boolean isTokenValid(String token) {
        Claims claims = getClaims(token);

        if(claims != null) {
            String email = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());

            if(email != null && expirationDate != null && now.before(expirationDate)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
