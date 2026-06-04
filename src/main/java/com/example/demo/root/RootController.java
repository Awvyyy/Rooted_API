package com.example.demo.root;

import com.example.demo.root.dto.request.CreateRootRequest;
import com.example.demo.root.dto.request.UpdateRootDescriptionRequest;
import com.example.demo.root.dto.response.RootResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/update")
    public RootResponse updateRoot(
            @RequestBody UpdateRootDescriptionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return rootService.updateRootDescription(request, jwt.getSubject());
    }
}
