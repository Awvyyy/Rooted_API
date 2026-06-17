package com.example.demo.leaf;

import com.example.demo.branch.Branch;
import com.example.demo.branch.BranchRepository;
import com.example.demo.leaf.dto.request.CreateLeafRequest;
import com.example.demo.leaf.dto.request.DeleteLeafRequest;
import com.example.demo.leaf.dto.request.EditLeafRequest;
import com.example.demo.leaf.dto.request.UniqueLeaf;
import com.example.demo.leaf.dto.response.DeleteLeafResponse;
import com.example.demo.leaf.dto.response.LeafResponse;
import com.example.demo.messaging.LeafLike;
import com.example.demo.messaging.LeafLikeRepository;
import com.example.demo.outbox.OutboxEventService;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public LeafResponse createLeaf(
            CreateLeafRequest request,
            String email
    ) {
        User user = getVerifiedUser(email);

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found"
                ));

        verifyLeafUnique(request, user, branch);

        Leaf leaf = new Leaf(
                branch,
                user,
                request.commentary(),
                0
        );

        leafRepository.save(leaf);

        return toResponse(leaf);
    }

    public LeafResponse editLeaf(
            String commentary,
            EditLeafRequest request,
            String email
    ) {
        User user = getVerifiedUser(email);

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found"
                ));

        Leaf leaf = leafRepository
                .findLeafByCommentaryAndUserAndBranch(commentary, user, branch)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        boolean changed =
                !leaf.getCommentary().equals(request.commentary())
                        || !leaf.getBranch().equals(branch);

        if (changed) {
            verifyLeafUnique(request, user, branch);
        }

        leaf.changeCommentary(request.commentary());

        leafRepository.save(leaf);

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

        boolean alreadyLiked = leafLikeRepository.existsByLeaf_IdAndUser_Id(
                leafId,
                user.getId()
        );

        if (alreadyLiked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Leaf already liked"
            );
        }

        LeafLike leafLike = new LeafLike(leaf, user);
        leafLikeRepository.save(leafLike);

        Long rootId = leaf.getBranch().getRoot().getId();

        outboxEventService.saveLeafLikedEvent(
                leafId,
                rootId,
                user.getId()
        );
    }

    @Transactional
    public DeleteLeafResponse deleteLeaf(
            String commentary,
            String email,
            DeleteLeafRequest request
    ) {
        User user = getVerifiedUser(email);

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found"
                ));

        Leaf leaf = leafRepository
                .findLeafByCommentaryAndUserAndBranch(commentary, user, branch)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        leafRepository.delete(leaf);

        return new DeleteLeafResponse("Leaf: " + commentary + " deleted successfully");
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