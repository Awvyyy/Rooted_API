package com.example.demo.root;

import com.example.demo.root.dto.request.CreateRootRequest;
import com.example.demo.root.dto.request.UpdateRootDescriptionRequest;
import com.example.demo.root.dto.response.RootResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootServiceTest {

    @Mock
    UserService userService;

    @Mock
    RootRepository rootRepository;

    @InjectMocks
    RootService rootService;

    @Test
    void createRoot_whenUserIsVerifiedAndTitleIsFree_createsRoot() {
        User user = verifiedUser(1L);
        CreateRootRequest request = new CreateRootRequest("Java", "Java discussion");

        when(userService.validateUser("aga@example.com")).thenReturn(user);
        when(rootRepository.existsByTitle("Java")).thenReturn(false);

        RootResponse response = rootService.createRoot(request, "aga@example.com");

        assertThat(response.title()).isEqualTo("Java");
        assertThat(response.description()).isEqualTo("Java discussion");
        assertThat(response.authorUsername()).isEqualTo("aga");
        verify(rootRepository).save(any(Root.class));
    }

    @Test
    void createRoot_whenEmailIsNotVerified_throwsForbidden() {
        User user = new User("aga", "hash", "aga@example.com", "EE");
        CreateRootRequest request = new CreateRootRequest("Java", "Java discussion");

        when(userService.validateUser("aga@example.com")).thenReturn(user);

        assertThatThrownBy(() -> rootService.createRoot(request, "aga@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");

        verify(rootRepository, never()).save(any());
    }

    @Test
    void createRoot_whenTitleIsTaken_throwsConflict() {
        User user = verifiedUser(1L);
        CreateRootRequest request = new CreateRootRequest("Java", "Java discussion");

        when(userService.validateUser("aga@example.com")).thenReturn(user);
        when(rootRepository.existsByTitle("Java")).thenReturn(true);

        assertThatThrownBy(() -> rootService.createRoot(request, "aga@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(rootRepository, never()).save(any());
    }

    @Test
    void updateRootDescription_whenUserIsOwner_updatesDescription() {
        User user = verifiedUser(1L);
        Root root = new Root("Java", "Old description", 0, user);
        UpdateRootDescriptionRequest request = new UpdateRootDescriptionRequest("New description");

        when(userService.validateUser("aga@example.com")).thenReturn(user);
        when(rootRepository.findByTitle("Java")).thenReturn(Optional.of(root));

        RootResponse response = rootService.updateRootDescription("Java", request, "aga@example.com");

        assertThat(root.getDescription()).isEqualTo("New description");
        assertThat(response.description()).isEqualTo("New description");
    }

    @Test
    void updateRootDescription_whenUserIsNotOwner_throwsForbidden() {
        User owner = verifiedUser(1L);
        User anotherUser = verifiedUser(2L);
        Root root = new Root("Java", "Old description", 0, owner);
        UpdateRootDescriptionRequest request = new UpdateRootDescriptionRequest("New description");

        when(userService.validateUser("another@example.com")).thenReturn(anotherUser);
        when(rootRepository.findByTitle("Java")).thenReturn(Optional.of(root));

        assertThatThrownBy(() -> rootService.updateRootDescription("Java", request, "another@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    private User verifiedUser(Long id) {
        User user = new User("aga", "hash", "aga@example.com", "EE");
        user.setEmailVerified(true);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
