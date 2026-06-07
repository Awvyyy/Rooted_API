package com.example.demo.branch;

import com.example.demo.branch.dto.request.CreateBranchRequest;
import com.example.demo.branch.dto.request.UpdateBranchDescriptionRequest;
import com.example.demo.branch.dto.response.BranchResponse;
import com.example.demo.root.Root;
import com.example.demo.root.RootRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BranchService {

    private final UserService userService;
    private final RootRepository rootRepository;
    private final BranchRepository branchRepository;

    public BranchService(UserService userService, RootRepository rootRepository, BranchRepository branchRepository ){

        this.userService = userService;
        this.branchRepository = branchRepository;
        this.rootRepository = rootRepository;
    }

    @Transactional
    public BranchResponse updateBranch(UpdateBranchDescriptionRequest request, String email, String title){
        User user = userService.validateUser(email);

        if (rootRepository.existsByTitle(title) && rootRepository.existsByDescription(request.newDescription())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Branch with this title and description already exists");
        }

        Branch branch = branchRepository.findBranchByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        branch.setDescription(request.newDescription());

        return toResponse(branch);
    }

    @Transactional
    public BranchResponse createBranch(CreateBranchRequest request, String email){
        User user = userService.validateUser(email);

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verify your email first");
        }

        if (branchRepository.existsBranchByTitle(request.title()) && branchRepository.existsBranchByDescription(request.description())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Branch with this title and description already exists");
        }
        boolean containsPhoto = false;
        if (!request.photoOriginalUrl().isEmpty()){
            containsPhoto = true;
        }

        Root root = rootRepository.findByTitle(request.rootTitle()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Root not found"));


        Branch branch = new Branch(root,
                user,
                request.title(),
                request.description(),
                0,
                0,
                request.tags(),
                containsPhoto,
                request.photoOriginalUrl());

        branchRepository.save(branch);
        return toResponse(branch);
    }

    public BranchResponse toResponse(Branch branch){
        User user = branch.getUser();
        return new BranchResponse(
                branch.getTitle(),
                branch.getDescription(),
                branch.getCommentsCount(),
                branch.getRating(),
                branch.getTags(),
                branch.isContainsPhoto(),
                branch.getPhotoOriginalUrl(),
                user.getUsername());
    }
}
