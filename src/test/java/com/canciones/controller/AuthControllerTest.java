package com.canciones.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Using deprecated annotation but required for WebMvcTest
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.canciones.dto.auth.AuthResponse;
import com.canciones.dto.auth.LoginRequest;
import com.canciones.dto.auth.RegisterRequest;
import com.canciones.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean // Note: This is deprecated but still needed for WebMvcTest until migrated to @WebMvcTest with explicit controller setup
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    
    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();
        
        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password")
                .build();
        
        authResponse = AuthResponse.builder()
                .token("jwt-token-example")
                .username("testuser")
                .build();
    }
    
    @Test
    void register_WithValidData_ShouldReturnToken() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-example"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .username("")
                .password("password")
                .email("invalid-email")
                .build();
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-example"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    void login_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        LoginRequest invalidRequest = LoginRequest.builder()
                .username("")
                .password("")
                .build();
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
