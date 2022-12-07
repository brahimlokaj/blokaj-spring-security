package org.blokaj.services.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.blokaj.exceptions.AuthenticationUserNotFoundException;
import org.blokaj.exceptions.RefreshTokenBadRequest;
import org.blokaj.models.entities.User;
import org.blokaj.models.requests.LoginUser;
import org.blokaj.models.requests.RefreshToken;
import org.blokaj.models.responses.MyProfile;
import org.blokaj.models.responses.Response;
import org.blokaj.models.responses.ResponseToken;
import org.blokaj.repositories.UserRepository;
import org.blokaj.security.AuthenticationTokenComponent;
import org.blokaj.security.AuthenticationUser;
import org.blokaj.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    /**
     * Inject UserRepository
     * <p>
     * If inject fails then throw NullPointerException
     */
    @NonNull
    private final UserRepository userRepository;

    /**
     * Inject PasswordEncoder
     * <p>
     * If inject fails then throw NullPointerException
     */
    @NonNull
    private final PasswordEncoder passwordEncoder;

    /**
     * Inject AuthenticationTokenComponent
     * <p>
     * If inject fails then throw NullPointerException
     */
    @NonNull
    private final AuthenticationTokenComponent authenticationTokenComponent;

    @Override
    public Response<ResponseToken> login(LoginUser loginUser) {
        Response<ResponseToken> response = new Response<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null);

        Optional<User> operationUser = userRepository.findUserByEmailOrUsername(loginUser.getUsernameOrEmail());
        if(operationUser.isEmpty()) {
            throw new AuthenticationUserNotFoundException("User not found");
        }

        User user = operationUser.get();

        if (passwordMatches(loginUser.getPassword(), user.getPassword()).equals(Boolean.TRUE)) {
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK.getReasonPhrase());
            response.setData(authenticationTokenComponent.getResponseToken(user));
        }

        return response;
    }

    @Override
    public Response<ResponseToken> refreshToken(RefreshToken refreshToken) {
        Response<ResponseToken> response = new Response<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null);

        try {
            String username = authenticationTokenComponent.getUsernameFromToken(refreshToken.getRefreshToken());
            Optional<User> operationUser = userRepository.findUserByEmailOrUsername(username);
            if (operationUser.isEmpty()) {
                throw new AuthenticationUserNotFoundException("User not found");
            }

            User user = operationUser.get();
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK.getReasonPhrase());
            response.setData(new ResponseToken(authenticationTokenComponent.generateAccessToken(user), null));


        } catch (Exception e) {
            throw new RefreshTokenBadRequest(e.getMessage());
        }

        return response;
    }

    @Override
    public Response<MyProfile> getMyProfile() {
        Response<MyProfile> response = new Response<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null);
        AuthenticationUser authenticationUser = (AuthenticationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        response.setData(userRepository.getMyProfile(authenticationUser.getId()));

        return response;
    }

    /**
     * Check the password if it is valid
     *
     * @param rawPassword String
     * @param encodedPassword String
     *
     * @return Boolean
     */
    private Boolean passwordMatches(String rawPassword, String encodedPassword) {
        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
