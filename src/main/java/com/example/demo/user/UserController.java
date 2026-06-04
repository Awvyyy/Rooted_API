package com.example.demo.user;

import com.example.demo.user.dto.request.*;
import com.example.demo.user.dto.response.ChangeUserDataResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/settings")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/changePassword")
    public ChangeUserDataResponse changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUserPassword(request, jwt.getSubject());
    }

    @PatchMapping("/changeUsername")
    public ChangeUserDataResponse changeUsername(
            @RequestBody ChangeUsernameRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUsername(request, jwt.getSubject());
    }

    @PatchMapping("/changeEmail")
    public ChangeUserDataResponse changeEmail(
            @RequestBody ChangeEmailRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeEmail(request, jwt.getSubject());
    }

    @PatchMapping("/changeProfilePicture")
    public ChangeUserDataResponse changeProfilePicture(
            @RequestBody ChangeProfilePictureRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeProfilePicture(request, jwt.getSubject());
    }

    @PatchMapping("/changeCountry")
    public ChangeUserDataResponse changeCountry(
            @RequestBody ChangeCountryRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeCountry(request, jwt.getSubject());
    }
}