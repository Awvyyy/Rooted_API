package com.example.demo.branch;

import com.example.demo.branch.dto.request.CreateBranchRequest;
import com.example.demo.branch.dto.request.UpdateBranchDescriptionRequest;
import com.example.demo.branch.dto.response.BranchResponse;
import com.example.demo.branch.dto.response.DeleteBranchResponse;
import com.example.demo.root.Root;
import com.example.demo.root.RootRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class BranchService {

    private final UserService userService;
    private final RootRepository rootRepository;
    private final BranchRepository branchRepository;

    public BranchService(
            UserService userService,
            RootRepository rootRepository,
            BranchRepository branchRepository
    ) {
        this.userService = userService;
        this.rootRepository = rootRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public BranchResponse createBranch(CreateBranchRequest request, Long userId) {
        User user = getVerifiedUser(userId);
        Root root = getRootByTitle(request.rootTitle());

        checkBranchDuplicate(request.title(), request.description());

        boolean containsPhoto = StringUtils.hasText(request.photoOriginalUrl());

        Branch branch = new Branch(
                root,
                user,
                request.title(),
                request.description(),
                0,
                0,
                request.tags(),
                containsPhoto,
                request.photoOriginalUrl()
        );

        Branch savedBranch = branchRepository.save(branch);

        return toResponse(savedBranch);
    }

    @Transactional
    public BranchResponse updateBranch(
            UpdateBranchDescriptionRequest request,
            Long userId,
            String title
    ) {
        User user = userService.validateUserById(userId);
        Branch branch = getBranchByTitle(title);

        checkBranchOwner(branch, user);

        if (Objects.equals(branch.getDescription(), request.newDescription())) {
            return toResponse(branch);
        }

        if (branchRepository.existsBranchByTitleAndDescription(title, request.newDescription())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Branch with this title and description already exists"
            );
        }

        branch.setDescription(request.newDescription());

        return toResponse(branch);
    }

    @Transactional
    public DeleteBranchResponse deleteBranch(String title, Long userId) {
        User user = userService.validateUserById(userId);

        Branch branch = getBranchByTitle(title);

        checkBranchOwner(branch, user);

        branchRepository.delete(branch);

        return new DeleteBranchResponse(
                "Branch: " + branch.getTitle() + " deleted successfully!"
        );
    }

    private User getVerifiedUser(Long userId) {
        User user = userService.validateUserById(userId);

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Verify your email first"
            );
        }

        return user;
    }

    private Root getRootByTitle(String title) {
        return rootRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Root not found"
                ));
    }

    private Branch getBranchByTitle(String title) {
        return branchRepository.findBranchByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found"
                ));
    }

    private void checkBranchDuplicate(String title, String description) {
        if (branchRepository.existsBranchByTitleAndDescription(title, description)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Branch with this title and description already exists"
            );
        }
    }

    private void checkBranchOwner(Branch branch, User user) {
        if (!Objects.equals(branch.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to update this branch"
            );
        }
    }

    private BranchResponse toResponse(Branch branch) {
        User user = branch.getUser();

        return new BranchResponse(
                branch.getTitle(),
                branch.getDescription(),
                branch.getCommentsCount(),
                branch.getRating(),
                branch.getTags(),
                branch.isContainsPhoto(),
                branch.getPhotoOriginalUrl(),
                user.getUsername()
        );
    }
}
