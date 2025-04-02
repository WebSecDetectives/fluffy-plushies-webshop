package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.FluffyPlushiesIdentityApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FluffyPlushiesIdentityApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
class UserControllerApiTest {

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void register_givenEmptyRequestBody_returnBadRequest() throws Exception {
        mvc.perform(post("/identity/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}