package margoumi.com.margoumi.security.jwt;

import margoumi.com.margoumi.security.UserPrincipal;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IJwtProvider {
    String generateToken(UserPrincipal auth);

    Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response);

    boolean validateToken(HttpServletRequest request, HttpServletResponse response);
}
