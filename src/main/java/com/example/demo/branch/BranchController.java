package com.example.demo.branch;

import com.example.demo.branch.dto.request.CreateBranchRequest;
import com.example.demo.branch.dto.request.UpdateBranchDescriptionRequest;
import com.example.demo.branch.dto.response.BranchResponse;
import com.example.demo.branch.dto.response.DeleteBranchResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/branch")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping("/create")
    public BranchResponse createBranch(
            @Valid @RequestBody CreateBranchRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return branchService.createBranch(request, userIdFrom(jwt));
    }

    @PatchMapping("/update/{title}")
    public BranchResponse updateBranch(
            @PathVariable String title,
            @Valid @RequestBody UpdateBranchDescriptionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return branchService.updateBranch(
                request,
                userIdFrom(jwt),
                title
        );
    }

    @DeleteMapping("/delete/{title}")
    public DeleteBranchResponse deleteBranch(
            @PathVariable String title,
            @AuthenticationPrincipal Jwt jwt
    ){
        return branchService.deleteBranch(title, userIdFrom(jwt));
    }

    private Long userIdFrom(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
