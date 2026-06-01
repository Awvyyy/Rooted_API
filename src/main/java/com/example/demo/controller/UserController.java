package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.ChangeUserDataResponse;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/settings")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/changePassword")
    public ChangeUserDataResponse changePassword(@RequestBody ChangePasswordRequest request){
        return userService.changeUserPassword(request);
    }

    @PostMapping("/changeProfilePicture")
    public ChangeUserDataResponse changeProfilePicture(@RequestBody ChangeProfilePictureRequest request){
        return userService.changeProfilePicture(request);
    }

    @PostMapping("/changeUsername")
    public ChangeUserDataResponse changeUsername(@RequestBody ChangeUsernameRequest request){
        return userService.changeUsername(request);
    }

    @PostMapping("/changeEmail")
    public ChangeUserDataResponse changeEmail(@RequestBody ChangeEmailRequest request){
        return userService.changeEmail(request);
    }

    @PostMapping("/changeCountry")
    public ChangeUserDataResponse changeCountry(@RequestBody ChangeCountryRequest request){
        return userService.changeCountry(request);
    }







}
