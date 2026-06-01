package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.ChangeUserDataResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ChangeUserDataResponse changeUserPassword (ChangePasswordRequest request) {
        User user = validateUser(request);

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't use old password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        return toResponse(user);

    }

    @Transactional
    public ChangeUserDataResponse changeUsername(ChangeUsernameRequest request){

        User user = validateUser(request);

        if (userRepository.existsByUsername(request.newUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        user.rename(request.newUsername());

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeEmail(ChangeEmailRequest request){

        User user = validateUser(request);

        if (userRepository.existsByEmail(request.newEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        user.changeEmail(request.newEmail());

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeProfilePicture(ChangeProfilePictureRequest request){

        User user = validateUser(request);

        if (user.getProfilePictureUrl().equals(request.profilePictureUrl())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Choose new profile picture");
        }

        user.setContainsProfilePicture(true);
        user.setProfilePictureUrl(request.profilePictureUrl());

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeCountry(ChangeCountryRequest request){

        User user = validateUser(request);

        if (user.getCountryCode().equals(request.newCountryCode())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Choose new country");
        }

        user.setCountryCode(request.newCountryCode());

        return toResponse(user);
    }

//    @Transactional
//    public ChangeUserDataResponse incrementUserCommends(IncrementUserCommendsRequest request){
//    }

    public ChangeUserDataResponse toResponse(User user){
        return new ChangeUserDataResponse(
                user.getUsername(),
                user.getEmail(),
                user.getCountryCode(),
                user.isContainsProfilePicture(),
                user.getProfilePictureUrl(),
                user.getCountryCode(),
                user.getCommends(),
                user.getMessages()
        );
    }

    public User validateUser (AuthRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }
        return user;
    }
}
