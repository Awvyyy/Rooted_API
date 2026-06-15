package com.example.demo.user;

import com.example.demo.user.dto.response.ChangeUserDataResponse;
import com.example.demo.user.dto.request.*;
import com.example.demo.user.dto.response.DeleteUserResponse;
import com.example.demo.user.dto.response.GetUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ChangeUserDataResponse changeUserPassword (ChangePasswordRequest request, String email) {
        User user = validateUser(email);

        if (passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wrong Password"
            );
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Can't use old password"
            );
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeUsername(ChangeUsernameRequest request, String email){

        User user = validateUser(email);

        if (userRepository.existsByUsername(request.newUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already registered"
            );
        }

        user.rename(request.newUsername());
        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeEmail(ChangeEmailRequest request, String email) {
        User user = validateUser(email);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wrong password"
            );
        }

        if (userRepository.existsByEmail(request.newEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        user.changeEmail(request.newEmail());
        user.setEmailVerified(false);
        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeProfilePicture(ChangeProfilePictureRequest request, String email){

        User user = validateUser(email);

        if (Objects.equals(user.getProfilePictureUrl(), request.profilePictureUrl())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Choose new profile picture"
            );
        }

        user.setContainsProfilePicture(true);
        user.setProfilePictureUrl(request.profilePictureUrl());

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeCountry(ChangeCountryRequest request, String email){

        User user = validateUser(email);

        if (user.getCountryCode().equals(request.newCountryCode())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Choose new country"
            );
        }

        user.setCountryCode(request.newCountryCode());

        return toResponse(user);
    }

    public GetUserResponse getUser (String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return new GetUserResponse(
                user.getUsername(),
                user.getContainsProfilePicture(),
                user.getProfilePictureUrl(),
                user.getCommends(),
                user.getMessages(),
                user.getCountryCode());
    }

    @Transactional
    public DeleteUserResponse deleteUser (DeleteUserRequest request, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wrong email or password"
            );
        }

        userRepository.delete(user);

        return new DeleteUserResponse(
                "User: " + user.getUsername() + " deleted successfully"
        );
    }

    /// todo
//    @Transactional
//    public ChangeUserDataResponse incrementUserCommends(){
//
//        User user = validateUser(email);
//        user.setCommends(user.getCommends() + 1);
//        return toResponse(user);
//
//    }
//
//    @Transactional
//    public ChangeUserDataResponse incrementUserMessages(){
//
//        User user = validateUser(email);
//        user.setMessages(user.getMessages() + 1);
//        return toResponse(user);
//    }

    private ChangeUserDataResponse toResponse(User user) {
        return new ChangeUserDataResponse(
                user.getUsername(),
                user.getEmail(),
                user.getCountryCode(),
                user.isContainsProfilePicture(),
                user.getProfilePictureUrl(),
                user.getCommends(),
                user.getMessages()
        );
    }

    public User validateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }
}