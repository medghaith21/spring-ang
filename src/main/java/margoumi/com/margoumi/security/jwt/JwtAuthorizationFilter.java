package margoumi.com.margoumi.security.jwt;

import lombok.extern.slf4j.XSlf4j;
import margoumi.com.margoumi.security.CustomUserDetaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class JwtAuthorizationFilter extends BasicAuthenticationFilter {




    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetaisService userDetaisService;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, CustomUserDetaisService userDetaisService) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.userDetaisService = userDetaisService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/api/internal");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = jwtProvider.getAuthentication(request, response);

        if (authentication != null && jwtProvider.validateToken(request, response) ){
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // filterChain.doFilter(request, response);

        String header = request.getHeader("Authorization");


        if(header != null && header.startsWith("Bearer ")) {

            UsernamePasswordAuthenticationToken auth = getAuthentication(header.substring(7), request);
            if(auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token, HttpServletRequest request) {

        if(jwtProvider.isTokenValid(token)) {
            String email = jwtProvider.getEmail(token);
            UserDetails user = userDetaisService.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        return null;
    }
}
