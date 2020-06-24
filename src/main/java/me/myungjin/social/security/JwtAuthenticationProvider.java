package me.myungjin.social.security;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class JwtAuthenticationProvider extends DaoAuthenticationProvider {

    private final Jwt jwt;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationProvider(Jwt jwt, UserDetailsService userDetailsService) {
        this.jwt = jwt;
        this.userDetailsService = userDetailsService;
        setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        return processUserAuthentication(new AuthenticationRequest(String.valueOf(authenticationToken.getPrincipal()), String.valueOf(authenticationToken.getCredentials())));

    }

    private Authentication processUserAuthentication(AuthenticationRequest request) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(request.getPrincipal());
            User user = userPrincipal.getUser();
            UsernamePasswordAuthenticationToken authenticated =
                    new UsernamePasswordAuthenticationToken(user.getSeq(), null, createAuthorityList(user.getRole().value()));
            String apiToken = user.newApiToken(jwt, new String[]{user.getRole().value()});
            authenticated.setDetails(new AuthenticationResult(apiToken, user));
            return authenticated;
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

}
