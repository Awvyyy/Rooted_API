package com.example.demo.service;

import com.example.demo.dto.request.ChangeEmailRequest;
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.ChangeProfilePictureRequest;
import com.example.demo.dto.request.ChangeUsernameRequest;
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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't use old password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        return toResponse(user);

    }

    @Transactional
    public ChangeUserDataResponse changeUsername(ChangeUsernameRequest request){

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        if (userRepository.existsByUsername(request.newUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        user.rename(request.newUsername());

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeEmail(ChangeEmailRequest request){

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        if (userRepository.existsByEmail(request.newEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        user.changeEmail(request.newEmail());

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeProfilePicture(ChangeProfilePictureRequest request){

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        if (user.getProfilePictureUrl().equals(request.profilePictureUrl())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Choose new profile picture");
        }

        user.setContainsProfilePicture(true);
        user.setProfilePictureUrl(request.profilePictureUrl());

        return toResponse(user);
    }

    public ChangeUserDataResponse toResponse(User user){
        return new ChangeUserDataResponse(
                user.getUsername(),
                user.getEmail(),
                user.getCountryCode(),
                user.isContainsProfilePicture(),
                user.getProfilePictureUrl());

    }
}
