package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.FluffyPlushiesIdentityApplication;
import com.dlshomies.fluffyplushies.config.FakerTestConfig;
import com.dlshomies.fluffyplushies.config.TestDataConfig;
import com.dlshomies.fluffyplushies.security.JwtUtil;
import com.dlshomies.fluffyplushies.util.TestDataUtil;
import com.dlshomies.fluffyplushies.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FluffyPlushiesIdentityApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({FakerTestConfig.class, TestDataConfig.class})
@AutoConfigureMockMvc
class UserControllerApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private TestDataUtil testDataUtil;

    private static final String DATA_PROVIDER_PATH = "com.dlshomies.fluffyplushies.api.TestDataProvider";

    public static final String STRONG_PASSWORD = "Str0ngP@ssw0rd";

    @BeforeEach
    void setUp() {
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
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDataUtil.userRequestWithDefaults())))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#nullAndEmptyFieldProvider")
    void registerUser_givenUsernameIsNullOrEmpty_returnBadRequest(Consumer<UserRequest.UserRequestBuilder<?, ?>> modifier) throws Exception {
        var builder = UserRequest.builder()
                .email(testDataUtil.emailAddress())
                .phone(testDataUtil.phoneNumber())
                .password(STRONG_PASSWORD)
                .address(testDataUtil.addressRequest())
                .username(testDataUtil.username());

        modifier.accept(builder);

        var userRequest = builder.build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "us",
            "usernameOneCharacterTooManyXXXX", // 31 char
            "usernameOneCharacterTooManyXXXXX"// 32 char
    })
    void registerUser_givenUsernameHasInvalidLength_returnBadRequest(String username) throws Exception {
        var userRequest = testDataUtil.userRequestWithUsername(username);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
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
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

    }
/*
    @Test
    void registerAdmin_givenCallerIsAdmin_returnOk() throws Exception {
        var username = "admin";
        var userRequest = UserRequest.builder()
                        .username(username)
                                .email(TestDataUtil.emailAddress())

        mvc.perform(post("/users/admin")
                        .header("Authorization", "Bearer" + jwtUtil.generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }*/

}