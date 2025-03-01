package com.BeeOranized.BeeOranized.test;


import com.BeeOranized.BeeOranized.ControllerVue.AuthControllerVue;
import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import com.BeeOranized.BeeOranized.Strategy.AuthContext;
import com.BeeOranized.BeeOranized.Strategy.JwtAuthStrategy;
import com.BeeOranized.BeeOranized.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthControllerVue.class)
public class AuthControllerVueTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private AuthContext authContext;

    @Mock
    private JwtAuthStrategy jwtAuthStrategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignin_Success() throws Exception {
        // Données d'entrée
        LoginRequestDto loginRequest = new LoginRequestDto("ahmed@gmail.com", "aahhmmeedd");

        // Configuration des mocks
        when(authContext.executeAuthentication(eq(loginRequest), any(Model.class)))
                .thenReturn("redirect:/listuser");

        // Effectuer la requête POST
        mockMvc.perform(post("/signinVue")
                        .flashAttr("loginRequest", loginRequest) // Envoi du DTO dans la requête
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())  // Attente d'une redirection
                .andExpect(view().name("redirect:/listuser"));  // Attente de la vue de redirection
    }

    @Test
    public void testSignin_Failure_InvalidCredentials() throws Exception {
        // Données d'entrée
        LoginRequestDto loginRequest = new LoginRequestDto("ahmed@gmail.com", "aahhmmeedd");

        // Configuration des mocks
        when(authContext.executeAuthentication(eq(loginRequest), any(Model.class)))
                .thenReturn("login");

        // Effectuer la requête POST
        mockMvc.perform(post("/signinVue")
                        .flashAttr("loginRequest", loginRequest) // Envoi du DTO dans la requête
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())  // Attente d'une réponse OK
                .andExpect(view().name("login"));  // Attente du retour à la page de connexion
    }
}
