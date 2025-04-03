package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.FluffyPlushiesIdentityApplication;
import com.dlshomies.fluffyplushies.api.config.FakerTestConfig;
import com.dlshomies.fluffyplushies.api.util.TestDataUtil;
import com.dlshomies.fluffyplushies.dto.AddressRequest;
import com.dlshomies.fluffyplushies.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private Faker faker;

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
        var addressRequest = AddressRequest.builder()
                .street(TestDataUtil.streetAddress())
                .postalCode(TestDataUtil.postcode())
                .city(TestDataUtil.city())
                .country(TestDataUtil.country())
                .build();

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
}