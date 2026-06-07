package com.example.demo.user;

import com.example.demo.user.dto.request.*;
import com.example.demo.user.dto.response.ChangeUserDataResponse;
import com.example.demo.user.dto.response.GetUserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public GetUserResponse getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @PatchMapping("/settings/changePassword")
    public ChangeUserDataResponse changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUserPassword(request, jwt.getSubject());
    }

    @PatchMapping("/settings/changeUsername")
    public ChangeUserDataResponse changeUsername(
            @RequestBody ChangeUsernameRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUsername(request, jwt.getSubject());
    }

    @PatchMapping("/settings/changeEmail")
    public ChangeUserDataResponse changeEmail(
            @RequestBody ChangeEmailRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeEmail(request, jwt.getSubject());
    }

    @PatchMapping("/settings/changeProfilePicture")
    public ChangeUserDataResponse changeProfilePicture(
            @RequestBody ChangeProfilePictureRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeProfilePicture(request, jwt.getSubject());
    }

    @PatchMapping("/settings/changeCountry")
    public ChangeUserDataResponse changeCountry(
            @RequestBody ChangeCountryRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeCountry(request, jwt.getSubject());
    }

    /// todo delete user + email verification

}