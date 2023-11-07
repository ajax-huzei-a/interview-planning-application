package intellistart.interviewplanning.security;

import intellistart.interviewplanning.exceptions.SecurityException;
import intellistart.interviewplanning.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtRequestFilter implements WebFilter {

    private final JwtUserDetailsService jwtUserDetailsService;


    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService, JwtUtil jwtUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestTokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        String email = null;
        String jwtToken;
        String name = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                email = jwtUtil.getEmailFromToken(jwtToken);
                name = jwtUtil.getNameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (UnsupportedJwtException e) {
                throw new SecurityException(SecurityException.SecurityExceptionProfile.UNSUPPORTED_TOKEN);
            } catch (MalformedJwtException e) {
                throw new SecurityException(SecurityException.SecurityExceptionProfile.MALFORMED_TOKEN);
            } catch (SignatureException e) {
                throw new SecurityException(SecurityException.SecurityExceptionProfile.BAD_TOKEN_SIGNATURE);
            } catch (ExpiredJwtException e) {
                throw new SecurityException(SecurityException.SecurityExceptionProfile.EXPIRED_TOKEN);
            }
        } else {
            jwtToken = null;
        }

        if (email != null) {
            return jwtUserDetailsService.loadUserByEmailAndName(email, name)
                    .flatMap(userDetails -> {
                        if (jwtUtil.validateToken(jwtToken, (JwtUserDetails) userDetails)) {
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())));
                        } else {
                            return Mono.empty();
                        }
                    });
        }

        return chain.filter(exchange);
    }
}
