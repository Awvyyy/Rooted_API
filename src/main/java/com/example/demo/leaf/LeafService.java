package com.example.demo.leaf;

import com.example.demo.leaf.dto.request.CreateLeafRequest;
import com.example.demo.leaf.dto.request.DeleteLeafRequest;
import com.example.demo.leaf.dto.request.EditLeafRequest;
import com.example.demo.leaf.dto.request.UniqueLeaf;
import com.example.demo.leaf.dto.response.DeleteLeafResponse;
import com.example.demo.leaf.dto.response.LeafResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LeafService {

    private final LeafRepository leafRepository;
    private final UserService userService;

    public LeafService(
            LeafRepository leafRepository,
            UserService userService
    ) {
        this.leafRepository = leafRepository;
        this.userService = userService;
    }

    public LeafResponse createLeaf(
            CreateLeafRequest request,
            String email
    ) {
        User user = getVerifiedUser(email);

        verifyLeafUnique(request, user);

        Leaf leaf = new Leaf(
                request.branch(),
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

        Leaf leaf = leafRepository
                .findLeafByCommentaryAndUserAndBranch(commentary, user, request.branch())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        boolean changed =
                !leaf.getCommentary().equals(request.commentary()) ||
                        !leaf.getBranch().equals(request.branch());

        if (changed) {
            verifyLeafUnique(request, user);
        }

        leaf.changeCommentary(request.commentary());

        leafRepository.save(leaf);

        return toResponse(leaf);
    }

    @Transactional
    public DeleteLeafResponse deleteLeaf(
            String commentary,
            String email,
            DeleteLeafRequest request
    ) {
        User user = getVerifiedUser(email);

        Leaf leaf = leafRepository
                .findLeafByCommentaryAndUserAndBranch(commentary, user, request.branch())
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
            User user
    ) {
        boolean exists = leafRepository.existsByCommentaryAndUserAndBranch(
                request.commentary(),
                user,
                request.branch()
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