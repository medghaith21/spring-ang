package margoumi.com.margoumi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import margoumi.com.margoumi.models.Role;
import margoumi.com.margoumi.security.jwt.JwtAuthorizationFilter;
import margoumi.com.margoumi.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Value("${authentication.internal-api-key}")
    private String internalApiKey;

    @Autowired
    private CustomUserDetaisService userDetaisService;

    @Autowired
    private JwtProvider jwtProvider;




    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetaisService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> new CorsConfiguration(corsConfiguration())).and().csrf().disable();
        // http.headers().cacheControl();

       //  http.headers().cacheControl();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests().antMatchers("/api/authentication/**").permitAll()
                .antMatchers("/product/**").permitAll().
                antMatchers("/category/**").permitAll().
                antMatchers("/api/countries/**").permitAll().
                antMatchers("/api/states/**").permitAll().
                antMatchers("/api/checkout/**").permitAll()
                .antMatchers("/api/checkout/purchase").permitAll()
                .antMatchers("/api/internal/**").hasRole(Role.SYSTEM_MANAGER.name())
                .antMatchers( "/api/orders/search/findByCustomerEmailOrderByDateCreatedDesc/**").permitAll()
                .antMatchers( "/api/wish/**").permitAll()
                .antMatchers("/comment/**").permitAll()
                .anyRequest().authenticated().and().httpBasic().and().headers().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.ORIGIN);
        http.addFilterBefore(jwtAuthorizationFilter(authenticationManager(), jwtProvider, userDetaisService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalApiAuthenticationFilter(), JwtAuthorizationFilter.class);

    }

    @Bean
    public InternalApiAuthenticationFilter internalApiAuthenticationFilter() {
        return new InternalApiAuthenticationFilter(internalApiKey);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, CustomUserDetaisService userDetails){
        return new JwtAuthorizationFilter(authenticationManager, jwtProvider, userDetails);
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200","http://localhost:8181"
                ).allowedHeaders("*")
                .allowedMethods(
                        "GET",
                        "PUT",
                        "POST",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                ).allowCredentials(true).maxAge(3600);;
    }



    @Bean
    CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration  = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control, Allow-Origin", "Content-Type", "Accept", "Authorization", "Origin, Accept", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Header" ));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization", "Access-Control-Request-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        return corsConfiguration ;
    }



    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }



    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }


}
