package io.github.xpakx.habitauth.user;

import io.github.xpakx.habitauth.clients.AccountPublisher;
import io.github.xpakx.habitauth.user.dto.AuthenticationRequest;
import io.github.xpakx.habitauth.user.dto.RegistrationRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    UserAccountRepository userRepository;
    @Autowired
    UserRoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    AccountPublisher publisher;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        UserAccount user = new UserAccount();
        user.setPassword(passwordEncoder.encode("password"));
        user.setUsername("Test");
        userId = userRepository.save(user).getId();
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void usernamesShouldBeUnique() {
        RegistrationRequest request = getRegRequest("Test", "password", "password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/register")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private RegistrationRequest getRegRequest(String username, String password, String password2) {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setPasswordRe(password2);
        return request;
    }

    @Test
    void passwordsShouldMatch() {
        RegistrationRequest request = getRegRequest("User", "password", "password2");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/register")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldRegisterNewUser() {
        RegistrationRequest request = getRegRequest("User", "password", "password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/register")
        .then()
                .statusCode(CREATED.value());
        assertThat(userRepository.count(), equalTo(2L));
    }

    @Test
    void shouldReturnTokenAfterRegistration() {
        RegistrationRequest request = getRegRequest("User", "password", "password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/register")
        .then()
                .statusCode(CREATED.value())
                .body("username", equalTo("User"))
                .body("token", notNullValue());
    }

    @Test
    void shouldNotAuthenticateIfPasswordIsWrong() {
        AuthenticationRequest request = getAuthRequest("Test", "password2");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/authenticate")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    private AuthenticationRequest getAuthRequest(String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    @Test
    void shouldNotAuthenticateIfUserDoesNotExist() {
        AuthenticationRequest request = getAuthRequest("User", "password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/authenticate")
        .then()
                .statusCode(FORBIDDEN.value());
    }

    @Test
    void shouldAuthenticateUser() {
        AuthenticationRequest request = getAuthRequest("Test", "password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/authenticate")
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldReturnTokenAfterAuthentication() {
        AuthenticationRequest request = getAuthRequest("Test", "password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/authenticate")
        .then()
                .statusCode(OK.value())
                .body("username", equalTo("Test"))
                .body("token", notNullValue());
    }
}