package com.example.demo.leaf;

import com.example.demo.leaf.dto.request.CreateLeafRequest;
import com.example.demo.leaf.dto.request.DeleteLeafRequest;
import com.example.demo.leaf.dto.request.EditLeafRequest;
import com.example.demo.leaf.dto.response.DeleteLeafResponse;
import com.example.demo.leaf.dto.response.LeafResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class LeafController {

    private final LeafService leafService;

    public LeafController(LeafService leafService) {
        this.leafService = leafService;
    }

    @PostMapping("/create")
    public LeafResponse createLeaf(
            @RequestBody CreateLeafRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.createLeaf(request, jwt.getSubject());
    }

    @PatchMapping("/{commentary}/edit")
    public LeafResponse editLeaf(
            @PathVariable String commentary,
            @RequestBody EditLeafRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.editLeaf(commentary, request, jwt.getSubject());
    }

    @DeleteMapping("/{commentary}/delete")
    public DeleteLeafResponse deleteLeaf(
            @PathVariable String commentary,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody DeleteLeafRequest request
    ) {
        return leafService.deleteLeaf(commentary, jwt.getSubject(), request);
    }

    @PostMapping("/{leafId}/like")
    public ResponseEntity<Void> likeLeaf(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long leafId
    ) {
        leafService.likeLeaf(leafId, jwt.getSubject());
        return ResponseEntity.accepted().build();
    }
}