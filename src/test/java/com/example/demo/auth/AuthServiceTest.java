package com.example.demo.auth;

import com.example.demo.auth.dto.request.LoginRequest;
import com.example.demo.auth.dto.request.RegisterRequest;
import com.example.demo.auth.dto.response.LoginResponse;
import com.example.demo.auth.dto.response.RegisterResponse;
import com.example.demo.emailVerification.VerificationService;
import com.example.demo.token.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    VerificationService verificationService;

    @InjectMocks
    AuthService authService;

    @Test
    void register_whenDataIsValid_savesUserAndSendsVerificationEmail() {
        RegisterRequest request = new RegisterRequest(
                "aga",
                "password123",
                "aga@example.com",
                "EE"
        );

        when(userRepository.existsByUsername("aga")).thenReturn(false);
        when(userRepository.existsByEmail("aga@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");

        RegisterResponse response = authService.userRegister(request);

        assertThat(response.username()).isEqualTo("aga");
        assertThat(response.email()).isEqualTo("aga@example.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("aga");
        assertThat(savedUser.getEmail()).isEqualTo("aga@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(savedUser.isEmailVerified()).isFalse();

        verify(verificationService).sendVerificationEmail(savedUser);
    }

    @Test
    void register_whenUsernameExists_throwsConflict() {
        RegisterRequest request = new RegisterRequest(
                "aga",
                "password123",
                "aga@example.com",
                "EE"
        );

        when(userRepository.existsByUsername("aga")).thenReturn(true);

        assertThatThrownBy(() -> authService.userRegister(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(userRepository, never()).save(any());
        verify(verificationService, never()).sendVerificationEmail(any());
    }

    @Test
    void register_whenEmailExists_throwsConflict() {
        RegisterRequest request = new RegisterRequest(
                "aga",
                "password123",
                "aga@example.com",
                "EE"
        );

        when(userRepository.existsByUsername("aga")).thenReturn(false);
        when(userRepository.existsByEmail("aga@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.userRegister(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(userRepository, never()).save(any());
        verify(verificationService, never()).sendVerificationEmail(any());
    }

    @Test
    void login_whenCredentialsAreValid_returnsBearerToken() {
        LoginRequest request = new LoginRequest("aga@example.com", "password123");
        User user = new User("aga", "hashed-password", "aga@example.com", "EE");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.userLogin(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void login_whenEmailDoesNotExist_throwsBadRequest() {
        LoginRequest request = new LoginRequest("missing@example.com", "password123");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.userLogin(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_whenPasswordIsWrong_throwsBadRequest() {
        LoginRequest request = new LoginRequest("aga@example.com", "wrong-password");
        User user = new User("aga", "hashed-password", "aga@example.com", "EE");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.userLogin(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(jwtService, never()).generateToken(any());
    }
}
