package com.example.demo.branch;

import com.example.demo.branch.dto.request.CreateBranchRequest;
import com.example.demo.branch.dto.request.UpdateBranchDescriptionRequest;
import com.example.demo.branch.dto.response.BranchResponse;
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
    public BranchResponse createBranch (@RequestBody CreateBranchRequest request, @AuthenticationPrincipal Jwt jwt){
        return branchService.createBranch(request, jwt.getSubject());
    }

    @PatchMapping("{title}/update")
    public BranchResponse updateBranch (@PathVariable String title, @RequestBody UpdateBranchDescriptionRequest request, @AuthenticationPrincipal Jwt jwt){
        return branchService.updateBranch(request, jwt.getSubject(), title);
    }

    /// todo: delete branch, add pathvariables to update/delete mappings.
}
