package com.example.demo.user;

import com.example.demo.user.dto.request.*;
import com.example.demo.user.dto.response.ChangeUserDataResponse;
import com.example.demo.user.dto.response.DeleteUserResponse;
import com.example.demo.user.dto.response.GetUserResponse;
import jakarta.validation.Valid;
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

    @PatchMapping("changePassword")
    public ChangeUserDataResponse changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUserPassword(request, jwt.getSubject());
    }

    @PatchMapping("changeUsername")
    public ChangeUserDataResponse changeUsername(
            @Valid @RequestBody ChangeUsernameRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUsername(request, jwt.getSubject());
    }

    @PatchMapping("changeEmail")
    public ChangeUserDataResponse changeEmail(
            @Valid @RequestBody ChangeEmailRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeEmail(request, jwt.getSubject());
    }

    @PatchMapping("changeProfilePicture")
    public ChangeUserDataResponse changeProfilePicture(
            @Valid @RequestBody ChangeProfilePictureRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeProfilePicture(request, jwt.getSubject());
    }

    @PatchMapping("changeCountry")
    public ChangeUserDataResponse changeCountry(
            @Valid @RequestBody ChangeCountryRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeCountry(request, jwt.getSubject());
    }

    @DeleteMapping("/delete")
    public DeleteUserResponse deleteUser(
            @Valid @RequestBody DeleteUserRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return userService.deleteUser(request, jwt.getSubject());
    }

    /// todo email verification

}