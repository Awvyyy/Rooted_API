package com.example.demo.user;

import com.example.demo.emailVerification.VerificationService;
import com.example.demo.user.dto.request.ChangeEmailRequest;
import com.example.demo.user.dto.request.ChangePasswordRequest;
import com.example.demo.user.dto.request.ChangeUsernameRequest;
import com.example.demo.user.dto.request.DeleteUserRequest;
import com.example.demo.user.dto.response.ChangeUserDataResponse;
import com.example.demo.user.dto.response.DeleteUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    VerificationService verificationService;

    @InjectMocks
    UserService userService;

    @Test
    void changePassword_whenOldPasswordIsCorrect_changesPassword() {
        User user = user("aga", "old-hash", "aga@example.com", 1L);
        ChangePasswordRequest request = new ChangePasswordRequest("old-password", "new-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        ChangeUserDataResponse response = userService.changeUserPassword(request, 1L);

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        assertThat(response.username()).isEqualTo("aga");
        assertThat(response.email()).isEqualTo("aga@example.com");
        assertThat(response.countryCode()).isEqualTo("EE");
    }

    @Test
    void changePassword_whenOldPasswordIsWrong_throwsBadRequest() {
        User user = user("aga", "old-hash", "aga@example.com", 1L);
        ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "old-hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.changeUserPassword(request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changePassword_whenNewPasswordEqualsOldPassword_throwsBadRequest() {
        User user = user("aga", "old-hash", "aga@example.com", 1L);
        ChangePasswordRequest request = new ChangePasswordRequest("old-password", "old-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserPassword(request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changeUsername_whenUsernameIsFree_renamesUser() {
        User user = user("oldname", "hash", "aga@example.com", 1L);
        ChangeUsernameRequest request = new ChangeUsernameRequest("newname");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newname")).thenReturn(false);

        ChangeUserDataResponse response = userService.changeUsername(request, 1L);

        assertThat(user.getUsername()).isEqualTo("newname");
        assertThat(response.username()).isEqualTo("newname");
    }

    @Test
    void changeUsername_whenUsernameIsTaken_throwsConflict() {
        User user = user("oldname", "hash", "aga@example.com", 1L);
        ChangeUsernameRequest request = new ChangeUsernameRequest("takenname");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("takenname")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUsername(request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

    }

    @Test
    void changeEmail_whenPasswordIsCorrectAndEmailIsFree_changesEmailAndMarksUnverified() {
        User user = user("aga", "hash", "old@example.com", 1L);
        user.setEmailVerified(true);
        ChangeEmailRequest request = new ChangeEmailRequest("new@example.com", "password123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        ChangeUserDataResponse response = userService.changeEmail(request, 1L);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(response.email()).isEqualTo("new@example.com");
        verify(verificationService).sendVerificationEmail(user);
    }

    @Test
    void deleteUser_whenPasswordIsCorrect_deletesUser() {
        User user = user("aga", "hash", "aga@example.com", 1L);
        DeleteUserRequest request = new DeleteUserRequest("password123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

        DeleteUserResponse response = userService.deleteUser(request, 1L);

        assertThat(response.message()).contains("deleted successfully");
        verify(userRepository).delete(user);
    }

    private User user(String username, String passwordHash, String email, Long id) {
        User user = new User(username, passwordHash, email, "EE");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
