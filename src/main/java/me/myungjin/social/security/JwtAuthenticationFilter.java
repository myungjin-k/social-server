package me.myungjin.social.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String headerKey;

    private final Jwt jwt;

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(Jwt jwt, String headerKey, AuthenticationManager authenticationManager) {
        this.jwt = jwt;
        this.headerKey = headerKey;
        this.authenticationManager = authenticationManager;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        AuthenticationRequest authRequest = null;
            try {
                authRequest = new ObjectMapper().readValue(request.getInputStream(), AuthenticationRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert authRequest != null;
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken( authRequest.getPrincipal(),authRequest.getCredentials());
            // Allow subclasses to set the "details" property
            setDetails(request, authenticationToken);

        // Authenticate user
        return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

            try {
                // Grab principal
                UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();
                User user = principal.getUser();
               //User user = userService.login(authResult.getPrincipal(), authResult.getCredentials());
               // JwtAuthenticationToken authenticated =
                //        new JwtAuthenticationToken(user.getSeq(), null, createAuthorityList(Role.ADMIN.value(), Role.USER.value()));
                String apiToken = user.newApiToken(jwt, new String[]{Role.ADMIN.value(), Role.USER.value()});
                //authResult.setDetails(new AuthenticationResult(apiToken, user));

                // Add token in response
                response.setHeader(headerKey, apiToken);

            } catch (NotFoundException e) {
                throw new UsernameNotFoundException(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new BadCredentialsException(e.getMessage());
            } catch (DataAccessException e) {
                throw new AuthenticationServiceException(e.getMessage(), e);
            }
    }

}
