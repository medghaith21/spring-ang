package margoumi.com.margoumi.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin"  , "*"                               );
        response.setHeader("Access-Control-Allow-Methods" , "POST, PUT, GET, OPTIONS, DELETE" );
        response.setHeader("Access-Control-Allow-Headers" , "Authorization, Content-Type"     );
        response.setHeader("Access-Control-Max-Age"       , "3600"                            );

        if("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            chain.doFilter(req, res);
        }
    }
    // ...
}
