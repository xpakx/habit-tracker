package io.github.xpakx.habitauth.user;

import io.github.xpakx.habitauth.clients.AccountPublisher;
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
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

}