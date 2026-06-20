package com.example.demo.user;

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

    @InjectMocks
    UserService userService;

    @Test
    void changePassword_whenOldPasswordIsCorrect_changesPassword() {
        User user = new User("aga", "old-hash", "aga@example.com", "EE");
        ChangePasswordRequest request = new ChangePasswordRequest("old-password", "new-password");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        ChangeUserDataResponse response = userService.changeUserPassword(request, "aga@example.com");

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        assertThat(response.username()).isEqualTo("aga");
        assertThat(response.email()).isEqualTo("aga@example.com");
        assertThat(response.countryCode()).isEqualTo("EE");
    }

    @Test
    void changePassword_whenOldPasswordIsWrong_throwsBadRequest() {
        User user = new User("aga", "old-hash", "aga@example.com", "EE");
        ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "old-hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.changeUserPassword(request, "aga@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changePassword_whenNewPasswordEqualsOldPassword_throwsBadRequest() {
        User user = new User("aga", "old-hash", "aga@example.com", "EE");
        ChangePasswordRequest request = new ChangePasswordRequest("old-password", "old-password");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserPassword(request, "aga@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changeUsername_whenUsernameIsFree_renamesUser() {
        User user = new User("oldname", "hash", "aga@example.com", "EE");
        ChangeUsernameRequest request = new ChangeUsernameRequest("newname");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newname")).thenReturn(false);

        ChangeUserDataResponse response = userService.changeUsername(request, "aga@example.com");

        assertThat(user.getUsername()).isEqualTo("newname");
        assertThat(response.username()).isEqualTo("newname");
    }

    @Test
    void changeUsername_whenUsernameIsTaken_throwsConflict() {
        User user = new User("oldname", "hash", "aga@example.com", "EE");
        ChangeUsernameRequest request = new ChangeUsernameRequest("takenname");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("takenname")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUsername(request, "aga@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    @Test
    void changeEmail_whenPasswordIsCorrectAndEmailIsFree_changesEmailAndMarksUnverified() {
        User user = new User("aga", "hash", "old@example.com", "EE");
        user.setEmailVerified(true);
        ChangeEmailRequest request = new ChangeEmailRequest("new@example.com", "password123");

        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        ChangeUserDataResponse response = userService.changeEmail(request, "old@example.com");

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(response.email()).isEqualTo("new@example.com");
    }

    @Test
    void deleteUser_whenPasswordIsCorrect_deletesUser() {
        User user = new User("aga", "hash", "aga@example.com", "EE");
        DeleteUserRequest request = new DeleteUserRequest("password123");

        when(userRepository.findByEmail("aga@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

        DeleteUserResponse response = userService.deleteUser(request, "aga@example.com");

        assertThat(response.message()).contains("deleted successfully");
        verify(userRepository).delete(user);
    }
}
