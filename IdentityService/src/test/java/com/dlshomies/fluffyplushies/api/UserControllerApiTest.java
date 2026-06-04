package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.FluffyPlushiesIdentityApplication;
import com.dlshomies.fluffyplushies.config.FakerTestConfig;
import com.dlshomies.fluffyplushies.config.MockRabbitMqConfig;
import com.dlshomies.fluffyplushies.config.TestDataConfig;
import com.dlshomies.fluffyplushies.dto.rest.AuthRequest;
import com.dlshomies.fluffyplushies.dto.rest.UpdatePasswordRequest;
import com.dlshomies.fluffyplushies.dto.rest.UpdateUserRequest;
import com.dlshomies.fluffyplushies.entity.Role;
import com.dlshomies.fluffyplushies.entity.User;
import com.dlshomies.fluffyplushies.repository.UserRepository;
import com.dlshomies.fluffyplushies.security.JwtUtil;
import com.dlshomies.fluffyplushies.service.UserService;
import com.dlshomies.fluffyplushies.util.TestDataUtil;
import com.dlshomies.fluffyplushies.dto.rest.CreateUserRequest;
import tools.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_=@Autowired)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FluffyPlushiesIdentityApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Import({FakerTestConfig.class, TestDataConfig.class, MockRabbitMqConfig.class})
@AutoConfigureMockMvc
class UserControllerApiTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TestDataUtil testDataUtil;
    private static final String DATA_PROVIDER_PATH = "com.dlshomies.fluffyplushies.api.TestDataProvider";
    private static final String STRONG_PASSWORD = "Str0ngP@ssw0rd";
    private String adminToken;
    private User savedUser;
    private String userToken;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void seedAdmin() {
        var adminRequest = testDataUtil.userRequestWithDefaults();
        var adminEntity = modelMapper.map(adminRequest, User.class);
        var savedAdmin = userService.registerAdminUser(adminEntity, adminRequest.getPassword());
        adminToken = jwtUtil.generateToken(savedAdmin).getToken();
    }

    @BeforeEach
    void seedUser() {
        var userRequest = testDataUtil.userRequestWithDefaults();
        var userEntity = modelMapper.map(userRequest, User.class);
        savedUser = userService.registerUser(userEntity, userRequest.getPassword());
        userToken = jwtUtil.generateToken(savedUser).getToken();
    }

    @Test
    void registerUser_givenEmptyRequestBody_returnBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_givenValidRequestBody_returnOk() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDataUtil.userRequestWithDefaults())))
                .andExpect(status().isOk());
    }

    @Test
    void login_givenValidCredentials_returnJwt() throws Exception {
        var authRequest = AuthRequest.builder()
                .username(savedUser.getUsername())
                .password(STRONG_PASSWORD)
                .build();

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expires_at").isNumber());
    }

    @Test
    void login_givenWrongPassword_returnUnauthorized() throws Exception {
        var authRequest = AuthRequest.builder()
                .username(savedUser.getUsername())
                .password("WrongP@ssw0rd")
                .build();

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_givenValidPassword_doesNotStoreRawPassword() {
        var savedPassword = userRepository.findById(savedUser.getId())
                .orElseThrow()
                .getEncodedPassword();

        assertNotEquals(STRONG_PASSWORD, savedPassword);
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#nullAndEmptyFieldProvider")
    void registerUser_givenUsernameIsNullOrEmpty_returnBadRequest(Consumer<CreateUserRequest.CreateUserRequestBuilder<?, ?>> modifier) throws Exception {
        var builder = CreateUserRequest.builder()
                .email(testDataUtil.emailAddress())
                .phone(testDataUtil.phoneNumber())
                .password(STRONG_PASSWORD)
                .address(testDataUtil.addressRequest())
                .username(testDataUtil.username());

        modifier.accept(builder);

        var userRequest = builder.build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "us",
            "usernameOneCharacterTooManyXXXX", // 31 char
            "usernameTwoCharacterTooManyXXXXX"// 32 char
    })
    void registerUser_givenUsernameHasInvalidLength_returnBadRequest(String username) throws Exception {
        var userRequest = testDataUtil.userRequestWithUsername(username);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", startsWith("size must be between")));
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#validEmailAddresses")
    void registerUser_givenEmailIsValid_returnOk(String email) throws Exception {
        var userRequest = testDataUtil.userRequestWithEmail(email);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#invalidEmailAddresses")
    void registerUser_givenEmailContainsInvalidCharacter_returnBadRequest(String email) throws Exception {
        var userRequest = testDataUtil.userRequestWithEmail(email);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "d@k.dk",                                                                           // Local part 1 char
            "Jfjhkljukkjjhbghfgjdfjnvnhvhvhnvhfjdkdfjhfghghdjcnvhfgjdjxjhvhgh@example.com"      // Local part 64 char
    })
    void registerUser_givenEmailHasValidLength_returnOk(String email) throws Exception {
        var userRequest = testDataUtil.userRequestWithEmail(email);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

    }

    @Test
    void registerAdmin_givenCallerIsAdmin_returnOk() throws Exception {
        var currentAdminUsername = "admin1";
        var currentAdminUser = testDataUtil.userWithUsername(currentAdminUsername);
        userService.registerAdminUser(currentAdminUser, STRONG_PASSWORD);
        var newAdminUsername = "admin2";
        var newAdminUserRequest = testDataUtil.userRequestWithUsername(newAdminUsername);

        mvc.perform(post("/users/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdminUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void registerAdmin_givenCallerIsUser_returnIsForbidden() throws Exception {
        var currentUsername = "user1";
        var currentUser = testDataUtil.userWithUsername(currentUsername);
        userService.registerUser(currentUser, STRONG_PASSWORD);
        var newAdminUsername = "user2";
        var newAdminUserRequest = testDataUtil.userRequestWithUsername(newAdminUsername);

        mvc.perform(post("/users/admin")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdminUserRequest)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {
            "Bearer",                     // prefix only
            "Foo abc.def.ghi",            // wrong scheme
    })
    void registerAdmin_givenMalformedOrNoToken_returnUnauthorized(String authHeader) throws Exception {
        var newAdminUserRequest = testDataUtil.userRequestWithDefaults();

        mvc.perform(post("/users/admin")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdminUserRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerAdmin_noAuthorizationHeader_returnUnauthorized() throws Exception {
        mvc.perform(post("/users/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDataUtil.userRequestWithUsername("newAdmin"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_givenExpiredToken_returnUnauthorized() throws Exception {
        mvc.perform(get("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + JwtTestUtil.expiredToken(savedUser, jwtSecret)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void getUser_givenMalformedToken_returnUnauthorized() throws Exception {
        mvc.perform(get("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void getUser_givenTamperedSignature_returnUnauthorized() throws Exception {
        var tampered = JwtTestUtil.tamperSignature(userToken);

        mvc.perform(get("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void getUser_givenValidlySignedTokenForUnknownUser_returnUnauthorized() throws Exception {
        var ghostToken = JwtTestUtil.signedToken(UUID.randomUUID(), "nonexistent-user", Role.USER,
                Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS), jwtSecret);

        mvc.perform(get("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + ghostToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void login_givenExpiredBearerToken_stillProcesses() throws Exception {
        var authRequest = AuthRequest.builder()
                .username(savedUser.getUsername())
                .password(STRONG_PASSWORD)
                .build();

        // Public endpoint: a stale token in the header must not break the request
        mvc.perform(post("/auth/login")
                        .header("Authorization", "Bearer " + JwtTestUtil.expiredToken(savedUser, jwtSecret))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void registerMerchant_givenCallerIsAdmin_returnOk() throws Exception {
        var merchantRequest = testDataUtil.userRequestWithUsername("merchant1");

        mvc.perform(post("/users/merchant")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void registerMerchant_givenCallerIsUser_returnForbidden() throws Exception {
        var merchantRequest = testDataUtil.userRequestWithUsername("merchant2");

        mvc.perform(post("/users/merchant")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerMerchant_givenValidRequest_savesMerchantRole() throws Exception {
        var merchantRequest = testDataUtil.userRequestWithUsername("merchant3");

        mvc.perform(post("/users/merchant")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantRequest)))
                .andExpect(status().isOk());

        var savedMerchant = userRepository.findByUsername(merchantRequest.getUsername()).orElseThrow();
        assertEquals(Role.MERCHANT, savedMerchant.getRole());
    }

    @Test
    void updateUser_givenCallerIsAdmin_returnOk() throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().phone("12341234").build();

        mvc.perform(patch("/users/{id}", savedUser.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_givenCallerIsSelf_returnOk() throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().phone("12341234").build();

        mvc.perform(patch("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_givenCallerIsNotSelfOrAdmin_returnForbidden() throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().phone("12341234").build();
        var currentUser = userService.registerUser(testDataUtil.userWithDefaults(), STRONG_PASSWORD);

        mvc.perform(patch("/users/{id}", currentUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {
            "Bearer",                     // prefix only
            "Foo abc.def.ghi",            // wrong scheme
    })
    void updateUser_givenMalformedOrNoToken_returnUnauthorized(String authHeader) throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().build();

        mvc.perform(patch("/users/{id}", savedUser.getId())
            .header("Authorization", authHeader)
            .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_givenNoAuthorizationHeader_returnUnauthorized() throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().build();

        mvc.perform(patch("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_givenUserIdDoesNotExist_returnNotFound() throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().build();

        mvc.perform(patch("/users/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_givenSoftDeletedUser_returnLocked() throws Exception {
        var updateUserRequest = UpdateUserRequest.builder().build();

        savedUser.setDeleted(true);
        userRepository.save(savedUser);

        mvc.perform(patch("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isLocked());
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#invalidPasswords")
    void updatePassword_givenInvalidPassword_returnBadRequest(String password) throws Exception {
        var updatePasswordRequest = UpdatePasswordRequest.builder().password(password).build();

        mvc.perform(patch("/users/{id}/password", savedUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_givenCallerIsAdmin_returnNoContent() throws Exception {
        mvc.perform(delete("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_givenCallerIsSelf_returnNoContent() throws Exception {
        mvc.perform(delete("/users/{id}", savedUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_givenCallerIsNotSelfOrAdmin_returnForbidden() throws Exception {
        var otherUser = userService.registerUser(testDataUtil.userWithDefaults(), STRONG_PASSWORD);

        mvc.perform(delete("/users/{id}", otherUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_givenUserDoesNotExist_returnNotFound() throws Exception {
        mvc.perform(delete("/users/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {
            "Bearer",                     // prefix only
            "Foo abc.def.ghi",            // wrong scheme
    })
    void deleteUser_givenMalformedOrNoToken_returnUnauthorized(String authHeader) throws Exception {
        mvc.perform(delete("/users/{id}", savedUser.getId())
                        .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_givenUserIsSoftDeleted_returnNoContent() throws Exception {
        var softDeletedUser = userService.registerUser(testDataUtil.userWithDeletedTrue(), STRONG_PASSWORD);

        mvc.perform(delete("/users/{id}", softDeletedUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
