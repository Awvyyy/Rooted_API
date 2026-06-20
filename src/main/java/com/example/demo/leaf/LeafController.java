package com.example.demo.leaf;

import com.example.demo.leaf.dto.request.CreateLeafRequest;
import com.example.demo.leaf.dto.request.EditLeafRequest;
import com.example.demo.leaf.dto.response.DeleteLeafResponse;
import com.example.demo.leaf.dto.response.LeafResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaves")
public class LeafController {

    private final LeafService leafService;

    public LeafController(LeafService leafService) {
        this.leafService = leafService;
    }

    @PostMapping
    public LeafResponse createLeaf(
            @Valid @RequestBody CreateLeafRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.createLeaf(request, jwt.getSubject());
    }

    @PatchMapping("/{leafId}")
    public LeafResponse editLeaf(
            @PathVariable Long leafId,
            @Valid @RequestBody EditLeafRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.editLeaf(leafId, request, jwt.getSubject());
    }

    @DeleteMapping("/{leafId}")
    public DeleteLeafResponse deleteLeaf(
            @PathVariable Long leafId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.deleteLeaf(leafId, jwt.getSubject());
    }

    @PostMapping("/{leafId}/like")
    public ResponseEntity<Void> likeLeaf(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long leafId
    ) {
        leafService.likeLeaf(leafId, jwt.getSubject());
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{leafId}/unlike")
    public ResponseEntity<Void> unlikeLeaf(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long leafId
    ){
        leafService.unlikeLeaf(leafId, jwt.getSubject());
        return ResponseEntity.accepted().build();
    }
}
