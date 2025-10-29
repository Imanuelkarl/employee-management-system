package ng.darum.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.darum.auth.config.SecurityConfig;
import ng.darum.auth.controllers.AuthenticationController;
import ng.darum.auth.dto.AuthResponse;
import ng.darum.auth.dto.UserRequest;
import ng.darum.auth.dto.UserResponse;
import ng.darum.auth.enums.Role;
import ng.darum.auth.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .email("test@example.com")
                .password("12345")
                .firstName("John")
                .lastName("Doe")
                .build();

        userResponse = new UserResponse(1L,  "test@example.com", Role.EMPLOYEE);

        authResponse = new AuthResponse(1L,  "test@example.com", Role.EMPLOYEE,"fake-jwt-token");
    }

    @Test
    void loginUser_ShouldReturnOkAndSetCookie() throws Exception {
        Mockito.when(authenticationService.loginUser(any(UserRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(header().exists("Set-Cookie"));
    }

}

