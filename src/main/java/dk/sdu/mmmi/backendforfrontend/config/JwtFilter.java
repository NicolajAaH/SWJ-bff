package dk.sdu.mmmi.backendforfrontend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Slf4j
//@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.split(" ")[1].trim(); //Only want part after Bearer
            log.warn(token);
            try {
                Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
                Date expirationDate = claims.getExpiration();
                if (expirationDate.before(new Date())) {
                    log.error("JWT token has expired");
                } else {
                    String userRole = claims.get("role").toString();
                    request.setAttribute("role", userRole);
                    request.setAttribute("id", claims.get("userId").toString());
                    setAuthenticationContext(token, request);
                }
                request.setAttribute("claims", claims);
                request.setAttribute("blog", request.getParameter("id"));
            } catch (Exception e) {
                log.error("Exception occurred while processing JWT token", e);
            }
        } else{
            log.info("No token registered - creating anonymous user");
            String newToken = Jwts.builder().compact();
            request.setAttribute("id", "0");
            request.setAttribute("role", "ROLE_ANONYMOUS");
            setAuthenticationContext(newToken, request);
        }


        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token, HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UserDetails getUserDetails(HttpServletRequest request){
        GrantedAuthority grantedRole = () -> request.getAttribute("role").toString();

        return new User(request.getAttribute("id").toString(), "", new ArrayList<>(){{
            add(grantedRole);
        }});
    }


}