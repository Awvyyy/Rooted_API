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

    @PostMapping("/create")
    public LeafResponse createLeaf(
            @Valid @RequestBody CreateLeafRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.createLeaf(request, userIdFrom(jwt));
    }

    @PatchMapping("/{leafId}")
    public LeafResponse editLeaf(
            @PathVariable Long leafId,
            @Valid @RequestBody EditLeafRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.editLeaf(leafId, request, userIdFrom(jwt));
    }

    @DeleteMapping("/{leafId}")
    public DeleteLeafResponse deleteLeaf(
            @PathVariable Long leafId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return leafService.deleteLeaf(leafId, userIdFrom(jwt));
    }

    @PostMapping("/like/{leafId}")
    public ResponseEntity<Void> likeLeaf(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long leafId
    ) {
        leafService.likeLeaf(leafId, userIdFrom(jwt));
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/unlike/{leafId}")
    public ResponseEntity<Void> unlikeLeaf(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long leafId
    ){
        leafService.unlikeLeaf(leafId, userIdFrom(jwt));
        return ResponseEntity.accepted().build();
    }

    private Long userIdFrom(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
