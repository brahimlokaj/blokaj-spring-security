package org.blokaj.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.blokaj.exceptions.AuthenticationUserNotFoundException;
import org.blokaj.exceptions.BadRequestException;
import org.blokaj.models.requests.LoginUser;
import org.blokaj.models.requests.RefreshToken;
import org.blokaj.models.responses.MyProfile;
import org.blokaj.models.responses.Response;
import org.blokaj.models.responses.ResponseToken;
import org.blokaj.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Authentication Controller
 */
@Log4j2
@RestController
@RequestMapping("${app.uri.base-uri}")
@RequiredArgsConstructor
public class AuthenticationController {

    @Value("${app.uri.login}")
    String login;
    @Value("${app.uri.refresh-token}")
    String refreshToken;
    @Value("${app.uri.my-profile}")
    String myProfile;


    /**
     * Inject AuthenticationService
     * <p>
     * If inject fails then throw NullPointerException
     */
    @NonNull
    private final AuthenticationService authenticationService;

    /**
     * Login method
     *
     * @param loginUser org.blokaj.models.requests.LoginUser
     * @return org.blokaj.models.responses.ResponseToken
     */
    @Operation(summary = "Login User", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response token",
                    content = @Content(schema = @Schema(implementation = ResponseToken.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = BadRequestException.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = AuthenticationUserNotFoundException.class)))})
    @PostMapping(value = "${app.uri.login}")
    public ResponseEntity<Response<ResponseToken>> login(@RequestBody @Valid LoginUser loginUser) {
        final String methodName = "login";
        log.info("{}: User login: {}", methodName, loginUser.getUsernameOrEmail());

        Response<ResponseToken> response = authenticationService.login(loginUser);
        if(response.getCode().equals(HttpStatus.UNAUTHORIZED.value())){
            log.error("{}: Unauthorized user: {}", methodName, loginUser.getUsernameOrEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        log.info("{}: Authorized user: {}", methodName, loginUser.getUsernameOrEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * RefreshToken method
     *
     * @param refreshToken org.blokaj.models.requests.RefreshToken
     * @return org.blokaj.models.responses.ResponseToken
     */
    @Operation(summary = "Refresh Token", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response token",
                    content = @Content(schema = @Schema(implementation = ResponseToken.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = BadRequestException.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = AuthenticationUserNotFoundException.class)))})
    @PostMapping(value = "${app.uri.refresh-token}")
    public ResponseEntity<Response<ResponseToken>> refreshToken(@RequestBody @Valid RefreshToken refreshToken) {
        final String methodName = "login";
        log.info("Refresh token: {}", methodName);

        Response<ResponseToken> response = authenticationService.refreshToken(refreshToken);
        if(response.getCode().equals(HttpStatus.UNAUTHORIZED.value())){
            log.error("Unauthorized refresh token: {}", methodName);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        log.info("Authorized refresh token: {}", methodName);
        return ResponseEntity.ok(response);
    }

    /**
     * getMyProfile method
     *
     * @return org.blokaj.models.responses.ResponseToken
     */
    @Operation(summary = "My Profile", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "My Profile Data",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = AuthenticationUserNotFoundException.class)))})
    @GetMapping(value = "${app.uri.my-profile}")
    public ResponseEntity<Response<MyProfile>> getMyProfile() {
        final String methodName = "getMyProfile";
        log.info("Get my profile: {}", methodName);

        return ResponseEntity.ok(authenticationService.getMyProfile());
    }

}
