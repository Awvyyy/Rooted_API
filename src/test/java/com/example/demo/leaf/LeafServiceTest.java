package com.example.demo.leaf;

import com.example.demo.branch.Branch;
import com.example.demo.branch.BranchRepository;
import com.example.demo.leaf.dto.request.CreateLeafRequest;
import com.example.demo.leaf.dto.request.EditLeafRequest;
import com.example.demo.leaf.dto.response.DeleteLeafResponse;
import com.example.demo.leaf.dto.response.LeafResponse;
import com.example.demo.messaging.LeafLike;
import com.example.demo.messaging.LeafLikeRepository;
import com.example.demo.outbox.OutboxEventService;
import com.example.demo.root.Root;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeafServiceTest {

    @Mock
    LeafRepository leafRepository;

    @Mock
    BranchRepository branchRepository;

    @Mock
    UserService userService;

    @Mock
    LeafLikeRepository leafLikeRepository;

    @Mock
    OutboxEventService outboxEventService;

    @InjectMocks
    LeafService leafService;

    @Test
    void createLeaf_whenUserVerifiedAndLeafIsUnique_createsLeaf() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);
        CreateLeafRequest request = new CreateLeafRequest(10L, "Nice post");

        when(userService.validateUserById(1L)).thenReturn(user);
        when(branchRepository.findBranchById(anyLong())).thenReturn(Optional.of(branch));
        when(leafRepository.existsByCommentaryAndUserAndBranch(
                request.commentary(),
                user,
                branch
        )).thenReturn(false);

        LeafResponse response = leafService.createLeaf(request, 1L);

        assertThat(response.authorName()).isEqualTo("aga");
        assertThat(response.branchName()).isEqualTo("Branch title");
        assertThat(response.commentary()).isEqualTo("Nice post");
        assertThat(response.rating()).isZero();

        verify(leafRepository).saveAndFlush(any(Leaf.class));

        assertThat(ReflectionTestUtils.getField(branch, "commentsCount"))
                .isEqualTo(1);
    }

    @Test
    void createLeaf_whenUserEmailNotVerified_throwsForbidden() {
        User user = new User("aga", "hash", "aga@example.com", "EE");
        ReflectionTestUtils.setField(user, "id", 1L);

        CreateLeafRequest request = new CreateLeafRequest(10L, "Nice post");

        when(userService.validateUserById(1L)).thenReturn(user);

        assertThatThrownBy(() -> leafService.createLeaf(request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");

        verify(branchRepository, never()).findBranchById(anyLong());
        verify(leafRepository, never()).saveAndFlush(any());
    }

    @Test
    void createLeaf_whenLeafAlreadyExists_throwsConflict() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);
        CreateLeafRequest request = new CreateLeafRequest(10L, "Nice post");

        when(userService.validateUserById(1L)).thenReturn(user);
        when(branchRepository.findBranchById(anyLong())).thenReturn(Optional.of(branch));
        when(leafRepository.existsByCommentaryAndUserAndBranch(
                request.commentary(),
                user,
                branch
        )).thenReturn(true);

        assertThatThrownBy(() -> leafService.createLeaf(request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(leafRepository, never()).saveAndFlush(any());
        assertThat(ReflectionTestUtils.getField(branch, "commentsCount"))
                .isEqualTo(0);
    }

    @Test
    void editLeaf_whenUserIsOwner_updatesCommentary() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);

        Leaf leaf = new Leaf(branch, user, "Old text", 0);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        EditLeafRequest request = new EditLeafRequest(10L, "New text");

        when(userService.validateUserById(1L)).thenReturn(user);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));
        when(branchRepository.findBranchById(anyLong())).thenReturn(Optional.of(branch));
        when(leafRepository.existsByCommentaryAndUserAndBranch(
                request.commentary(),
                user,
                branch
        )).thenReturn(false);

        LeafResponse response = leafService.editLeaf(100L, request, 1L);

        assertThat(leaf.getCommentary()).isEqualTo("New text");
        assertThat(response.commentary()).isEqualTo("New text");
    }

    @Test
    void editLeaf_whenUserIsNotOwner_throwsForbidden() {
        User owner = verifiedUser(1L);
        User anotherUser = verifiedUser(2L);

        Branch branch = branch(10L, owner);

        Leaf leaf = new Leaf(branch, owner, "Old text", 0);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        EditLeafRequest request = new EditLeafRequest(10L, "New text");

        when(userService.validateUserById(2L)).thenReturn(anotherUser);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));

        assertThatThrownBy(() -> leafService.editLeaf(100L, request, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");

        verify(branchRepository, never()).findBranchById(anyLong());
    }

    @Test
    void likeLeaf_whenNotLikedYet_savesLikeAndCreatesOutboxEvent() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);

        Leaf leaf = new Leaf(branch, user, "Nice post", 0);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        when(userService.validateUserById(1L)).thenReturn(user);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));

        leafService.likeLeaf(100L, 1L);

        verify(leafLikeRepository).saveAndFlush(any(LeafLike.class));
        verify(outboxEventService).saveLeafLikedEvent(100L, 10L, 1L);
    }

    @Test
    void likeLeaf_whenAlreadyLiked_throwsConflict() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);

        Leaf leaf = new Leaf(branch, user, "Nice post", 0);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        when(userService.validateUserById(1L)).thenReturn(user);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));

        doThrow(new DataIntegrityViolationException("duplicate like"))
                .when(leafLikeRepository)
                .saveAndFlush(any(LeafLike.class));

        assertThatThrownBy(() -> leafService.likeLeaf(100L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(outboxEventService, never())
                .saveLeafLikedEvent(anyLong(), anyLong(), anyLong());
    }

    @Test
    void unlikeLeaf_whenLiked_deletesLikeAndCreatesOutboxEvent() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);

        Leaf leaf = new Leaf(branch, user, "Nice post", 1);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        LeafLike leafLike = new LeafLike(leaf, user);

        when(userService.validateUserById(1L)).thenReturn(user);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));
        when(leafLikeRepository.findByLeaf_IdAndUser_Id(100L, 1L))
                .thenReturn(Optional.of(leafLike));

        leafService.unlikeLeaf(100L, 1L);

        verify(leafLikeRepository).delete(leafLike);
        verify(outboxEventService).saveLeafUnlikedEvent(100L, 10L, 1L);
    }

    @Test
    void unlikeLeaf_whenNotLiked_throwsConflict() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);

        Leaf leaf = new Leaf(branch, user, "Nice post", 0);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        when(userService.validateUserById(1L)).thenReturn(user);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));
        when(leafLikeRepository.findByLeaf_IdAndUser_Id(100L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> leafService.unlikeLeaf(100L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(leafLikeRepository, never()).delete(any());
        verify(outboxEventService, never())
                .saveLeafUnlikedEvent(anyLong(), anyLong(), anyLong());
    }

    @Test
    void deleteLeaf_whenUserIsOwner_deletesLeafLikesAndLeaf() {
        User user = verifiedUser(1L);
        Branch branch = branch(10L, user);

        ReflectionTestUtils.setField(branch, "commentsCount", 1);

        Leaf leaf = new Leaf(branch, user, "Nice post", 3);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        when(userService.validateUserById(1L)).thenReturn(user);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));

        DeleteLeafResponse response = leafService.deleteLeaf(100L, 1L);

        assertThat(response.message())
                .isEqualTo("Leaf: Nice post deleted successfully");

        verify(leafLikeRepository).deleteByLeaf_Id(100L);
        verify(leafRepository).delete(leaf);

        assertThat(ReflectionTestUtils.getField(branch, "commentsCount"))
                .isEqualTo(0);
    }

    @Test
    void deleteLeaf_whenUserIsNotOwner_throwsForbidden() {
        User owner = verifiedUser(1L);
        User anotherUser = verifiedUser(2L);

        Branch branch = branch(10L, owner);

        Leaf leaf = new Leaf(branch, owner, "Nice post", 0);
        ReflectionTestUtils.setField(leaf, "id", 100L);

        when(userService.validateUserById(2L)).thenReturn(anotherUser);
        when(leafRepository.findById(100L)).thenReturn(Optional.of(leaf));

        assertThatThrownBy(() -> leafService.deleteLeaf(100L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");

        verify(leafLikeRepository, never()).deleteByLeaf_Id(anyLong());
        verify(leafRepository, never()).delete(any());
    }

    private User verifiedUser(Long id) {
        User user = new User("aga", "hash", "aga@example.com", "EE");
        user.setEmailVerified(true);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Branch branch(Long id, User user) {
        Root root = new Root("Root title", "Root description", 0, user);

        Branch branch = new Branch(
                root,
                user,
                "Branch title",
                "Branch description",
                0,
                0,
                null,
                false,
                null
        );

        ReflectionTestUtils.setField(branch, "id", id);
        return branch;
    }
}
