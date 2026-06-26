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
        return userService.changeUserPassword(request, userIdFrom(jwt));
    }

    @PatchMapping("changeUsername")
    public ChangeUserDataResponse changeUsername(
            @Valid @RequestBody ChangeUsernameRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeUsername(request, userIdFrom(jwt));
    }

    @PatchMapping("changeEmail")
    public ChangeUserDataResponse changeEmail(
            @Valid @RequestBody ChangeEmailRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeEmail(request, userIdFrom(jwt));
    }

    @PatchMapping("changeProfilePicture")
    public ChangeUserDataResponse changeProfilePicture(
            @Valid @RequestBody ChangeProfilePictureRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeProfilePicture(request, userIdFrom(jwt));
    }

    @PatchMapping("changeCountry")
    public ChangeUserDataResponse changeCountry(
            @Valid @RequestBody ChangeCountryRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.changeCountry(request, userIdFrom(jwt));
    }

    @DeleteMapping("/delete")
    public DeleteUserResponse deleteUser(
            @Valid @RequestBody DeleteUserRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        return userService.deleteUser(request, userIdFrom(jwt));
    }

    /// todo email verification

    private Long userIdFrom(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

}
