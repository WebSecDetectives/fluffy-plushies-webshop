package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.FluffyPlushiesIdentityApplication;
import com.dlshomies.fluffyplushies.config.FakerTestConfig;
import com.dlshomies.fluffyplushies.util.TestDataUtil;
import com.dlshomies.fluffyplushies.dto.AddressRequest;
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
@Import(FakerTestConfig.class)
@AutoConfigureMockMvc
class UserControllerApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String DATA_PROVIDER_PATH = "com.dlshomies.fluffyplushies.api.TestDataProvider";

    @BeforeEach
    void setUp() {
    }

    @Test
    void register_givenEmptyRequestBody_returnBadRequest() throws Exception {
        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_givenValidRequestBody_returnOk() throws Exception {
        var addressRequest = TestDataUtil.addressRequest();

        objectMapper.writeValueAsString(addressRequest);

        var userRequest = UserRequest.builder()
                .username(TestDataUtil.username())
                .email(TestDataUtil.emailAddress())
                .phone(TestDataUtil.phoneNumber())
                .password("Str0ngP@ssw0rd")
                .address(addressRequest)
                .build();

        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#nullAndEmptyFieldProvider")
    void register_givenFieldIsNullOrEmpty_returnBadRequest(Consumer<UserRequest.UserRequestBuilder<?, ?>> modifier) throws Exception {
        var addressRequest = TestDataUtil.addressRequest();

        var builder = UserRequest.builder()
                .email(TestDataUtil.emailAddress())
                .phone(TestDataUtil.phoneNumber())
                .password("Str0ngP@ssw0rd")
                .address(addressRequest)
                .username(TestDataUtil.username());

        modifier.accept(builder);
        var userRequest = builder.build();

        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "us",
            "usernameOneCharacterTooManyXXXX", // 31 char
            "usernameOneCharacterTooManyXXXXX"// 32 char
    })
    void register_givenUsernameHasInvalidLength_returnBadRequest(String username) throws Exception {
        var addressRequest = TestDataUtil.addressRequest();

        var userRequest = UserRequest.builder()
                .email(TestDataUtil.emailAddress())
                .phone(TestDataUtil.phoneNumber())
                .password("Str0ngP@ssw0rd")
                .address(addressRequest)
                .username(username)
                .build();

        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username", startsWith("size must be between")));
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#validEmailAddresses")
    void register_givenEmailIsValid_returnOk(String email) throws Exception {
        var addressRequest = TestDataUtil.addressRequest();

        var userRequest = UserRequest.builder()
                .email(email)
                .phone(TestDataUtil.phoneNumber())
                .password("Str0ngP@ssw0rd")
                .address(addressRequest)
                .username(TestDataUtil.username())
                .build();

        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource(value = DATA_PROVIDER_PATH + "#invalidEmailAddresses")
    void register_givenEmailContainsInvalidCharacter_returnBadRequest(String email) throws Exception {
        var addressRequest = TestDataUtil.addressRequest();

        var userRequest = UserRequest.builder()
                .email(email)
                .phone(TestDataUtil.phoneNumber())
                .password("Str0ngP@ssw0rd")
                .address(addressRequest)
                .username(TestDataUtil.username())
                .build();

        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "d@k.dk",                                                                           // Local part 1 char
            "Jfjhkljukkjjhbghfgjdfjnvnhvhvhnvhfjdkdfjhfghghdjcnvhfgjdjxjhvhgh@example.com"      // Local part 64 char
    })
    void register_givenEmailHasValidLength_returnOk(String email) throws Exception {
        var addressRequest = TestDataUtil.addressRequest();

        var userRequest = UserRequest.builder()
                .email(email)
                .phone(TestDataUtil.phoneNumber())
                .password("Str0ngP@ssw0rd")
                .address(addressRequest)
                .username(TestDataUtil.username())
                .build();

        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

}