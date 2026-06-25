package com.example.demo.leaf;

import com.example.demo.branch.Branch;
import com.example.demo.branch.BranchRepository;
import com.example.demo.leaf.dto.request.CreateLeafRequest;
import com.example.demo.leaf.dto.request.EditLeafRequest;
import com.example.demo.leaf.dto.request.UniqueLeaf;
import com.example.demo.leaf.dto.response.DeleteLeafResponse;
import com.example.demo.leaf.dto.response.LeafResponse;
import com.example.demo.messaging.LeafLike;
import com.example.demo.messaging.LeafLikeRepository;
import com.example.demo.outbox.OutboxEventService;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class LeafService {

    private final LeafRepository leafRepository;
    private final BranchRepository branchRepository;
    private final UserService userService;
    private final LeafLikeRepository leafLikeRepository;
    private final OutboxEventService outboxEventService;

    public LeafService(
            LeafRepository leafRepository,
            BranchRepository branchRepository,
            UserService userService,
            LeafLikeRepository leafLikeRepository,
            OutboxEventService outboxEventService
    ) {
        this.leafRepository = leafRepository;
        this.branchRepository = branchRepository;
        this.userService = userService;
        this.leafLikeRepository = leafLikeRepository;
        this.outboxEventService = outboxEventService;
    }

    @Transactional
    public LeafResponse createLeaf(
            CreateLeafRequest request,
            String email
    ) {
        User user = getVerifiedUser(email);
        Branch branch = branchRepository.findBranchById(request.branchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        verifyLeafUnique(request, user, branch);

        Leaf leaf = new Leaf(
                branch,
                user,
                request.commentary(),
                0
        );

        leafRepository.saveAndFlush(leaf);
        branch.incrementCommentsCount();

        return toResponse(leaf);
    }

    @Transactional
    public LeafResponse editLeaf(
            Long leafId,
            EditLeafRequest request,
            String email
    ) {
        User user = getVerifiedUser(email);
        Leaf leaf = getOwnedLeaf(leafId, user);
        Branch branch = branchRepository.findBranchById(request.branchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        boolean changed =
                !leaf.getCommentary().equals(request.commentary())
                        || !Objects.equals(leaf.getBranch().getId(), branch.getId());

        if (changed) {
            verifyLeafUnique(request, user, branch);
        }

        leaf.changeCommentary(request.commentary());

        return toResponse(leaf);
    }

    @Transactional
    public void likeLeaf(Long leafId, String email) {
        User user = getVerifiedUser(email);

        Leaf leaf = leafRepository.findById(leafId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        try {
            LeafLike leafLike = new LeafLike(leaf, user);
            leafLikeRepository.saveAndFlush(leafLike);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Leaf already liked"
            );
        }

        outboxEventService.saveLeafLikedEvent(
                leafId,
                leaf.getBranch().getId(),
                user.getId()
        );
    }

    @Transactional
    public void unlikeLeaf(Long leafId, String email) {
        User user = getVerifiedUser(email);

        Leaf leaf = leafRepository.findById(leafId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        LeafLike leafLike = leafLikeRepository.findByLeaf_IdAndUser_Id(
                leafId,
                user.getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Leaf is not liked"
        ));

        leafLikeRepository.delete(leafLike);

        outboxEventService.saveLeafUnlikedEvent(
                leafId,
                leaf.getBranch().getId(),
                user.getId()
        );
    }

    @Transactional
    public DeleteLeafResponse deleteLeaf(
            Long leafId,
            String email
    ) {
        User user = getVerifiedUser(email);
        Leaf leaf = getOwnedLeaf(leafId, user);

        String commentary = leaf.getCommentary();
        Branch branch = leaf.getBranch();

        leafLikeRepository.deleteByLeaf_Id(leafId);
        leafRepository.delete(leaf);
        branch.decrementCommentsCount();

        return new DeleteLeafResponse(
                "Leaf: " + commentary + " deleted successfully"
        );
    }

    private Leaf getOwnedLeaf(Long leafId, User user) {
        Leaf leaf = leafRepository.findById(leafId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        if (!Objects.equals(leaf.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No access to leaf"
            );
        }

        return leaf;
    }

    private User getVerifiedUser(String email) {
        User user = userService.validateUser(email);

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Verify your email first"
            );
        }

        return user;
    }

    private void verifyLeafUnique(
            UniqueLeaf request,
            User user,
            Branch branch
    ) {
        boolean exists = leafRepository.existsByCommentaryAndUserAndBranch(
                request.commentary(),
                user,
                branch
        );

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Leaf already exists with commentary: " + request.commentary()
            );
        }
    }

    private LeafResponse toResponse(Leaf leaf) {
        return new LeafResponse(
                leaf.getUser().getUsername(),
                leaf.getBranch().getTitle(),
                leaf.getCommentary(),
                leaf.getRating()
        );
    }
}
