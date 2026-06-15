package com.example.demo.root;

import com.example.demo.root.dto.request.CreateRootRequest;
import com.example.demo.root.dto.request.UpdateRootDescriptionRequest;
import com.example.demo.root.dto.response.DeleteRootResponse;
import com.example.demo.root.dto.response.GetAllRoots;
import com.example.demo.root.dto.response.RootResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/root")
public class RootController {

    private final RootService rootService;
    public RootController(RootService rootService){
        this.rootService = rootService;
    }

    @PostMapping("/create")
    public RootResponse createRoot(
            @RequestBody CreateRootRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return rootService.createRoot(request, jwt.getSubject());
    }

    @PatchMapping("/{title}/update")
    public RootResponse updateRoot(
            @PathVariable String title,
            @RequestBody UpdateRootDescriptionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return rootService.updateRootDescription(
                title,
                request,
                jwt.getSubject()
        );
    }

    @GetMapping()
    public GetAllRoots getAllRoots(){
        return rootService.getAllRoots();
    }

    @GetMapping("/{title}")
    public RootResponse getRoot(@PathVariable String title){
        return rootService.getRoot(title);
    }

    @DeleteMapping("/{title}")
    public DeleteRootResponse deleteRootResponse(
            @PathVariable String title,
            @AuthenticationPrincipal Jwt jwt
    ){
        return rootService.deleteRoot(title, jwt.getSubject());
    }
}
