package org.blokaj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.blokaj.models.requests.LoginUser;
import org.blokaj.models.requests.RefreshToken;
import org.blokaj.models.responses.FieldError;
import org.blokaj.models.responses.MyProfile;
import org.blokaj.models.responses.Response;
import org.blokaj.models.responses.ResponseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuthenticationControllerTests {

    @Value("${app.uri.base-uri}")
    String baseUri;
    @Value("${app.uri.login}")
    String login;
    @Value("${app.uri.refresh-token}")
    String refreshToken;
    @Value("${app.uri.my-profile}")
    String myProfile;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String loginUriTemplate;
    private String refreshTokenUriTemplate;
    private String myProfileUriTemplate;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        loginUriTemplate = baseUri+login;
        refreshTokenUriTemplate = baseUri+refreshToken;
        myProfileUriTemplate = baseUri+myProfile;
    }

    @Test
    void login_userNameOrEmail_400() throws Exception {
        LoginUser loginUser = new LoginUser("", "123");

        MvcResult mvcResult = mockMvc.perform(post(loginUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser))
        ).andReturn();

        Response<List<FieldError>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals(400, response.getCode());
        assertEquals("usernameOrEmail", response.getData().get(0).getName());
    }

    @Test
    void login_password_400() throws Exception {
        LoginUser loginUser = new LoginUser("blokaj", "");

        MvcResult mvcResult = mockMvc.perform(post(loginUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser))
        ).andReturn();

        Response<List<FieldError>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals(400, response.getCode());
        assertEquals("password", response.getData().get(0).getName());
    }

    @Test
    void login_401() throws Exception {
        LoginUser loginUser = new LoginUser("blokaj", "1234");

        MvcResult mvcResult = mockMvc.perform(post(loginUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser))
        ).andReturn();

        Response<List<FieldError>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(401, mvcResult.getResponse().getStatus());
        assertEquals(401, response.getCode());
        assertNull(response.getData());
    }

    @Test
    void login_200() throws Exception {
        LoginUser loginUser = new LoginUser("blokaj", "123");

        MvcResult mvcResult = mockMvc.perform(post(loginUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser))
        ).andReturn();

        Response<ResponseToken> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getAccessToken());
        assertNotNull(response.getData().getRefreshToken());
    }

    @Test
    void login_refreshToken_400() throws Exception {
        RefreshToken refreshToken = new RefreshToken(null);
        MvcResult mvcResult = mockMvc.perform(post(refreshTokenUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshToken))
        ).andReturn();
        Response<List<FieldError>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals(400, response.getCode());
        assertEquals("refreshToken", response.getData().get(0).getName());
    }

    @Test
    void login_refreshTokenNotValid_400() throws Exception {
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString());
        MvcResult mvcResult = mockMvc.perform(post(refreshTokenUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshToken))
        ).andReturn();
        Response<String> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals(400, response.getCode());
        assertNotNull(response.getData());
    }

    @Test
    void login_refreshToken_200() throws Exception {
        LoginUser loginUser = new LoginUser("blokaj", "123");
        MvcResult mvcResult = mockMvc.perform(post(loginUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser))
        ).andReturn();
        Response<ResponseToken> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        RefreshToken refreshToken = new RefreshToken(response.getData().getRefreshToken());
        MvcResult mvcRefreshTokenResult = mockMvc.perform(post(refreshTokenUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshToken))
        ).andReturn();
        Response<ResponseToken> responseRefreshToken = objectMapper.readValue(mvcRefreshTokenResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(200, response.getCode());
        assertNull(responseRefreshToken.getData().getRefreshToken());
        assertNotNull(responseRefreshToken.getData().getAccessToken());
    }

    @Test
    void myProfile_200() throws Exception {
        LoginUser loginUser = new LoginUser("blokaj", "123");
        MvcResult mvcResult = mockMvc.perform(post(loginUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser))
        ).andReturn();
        Response<ResponseToken> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        MvcResult mvcMyProfileResult = mockMvc.perform(get(myProfileUriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+response.getData().getAccessToken())
        ).andReturn();
        Response<MyProfile> myProfileResponse = objectMapper.readValue(mvcMyProfileResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(200, myProfileResponse.getCode());
        assertNotNull(myProfileResponse.getData());
    }
}
