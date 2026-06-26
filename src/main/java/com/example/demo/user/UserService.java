package com.example.demo.user;

import com.example.demo.emailVerification.VerificationService;
import com.example.demo.user.dto.request.*;
import com.example.demo.user.dto.response.ChangeUserDataResponse;
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
    private final VerificationService verificationService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationService verificationService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
    }

    @Transactional
    public ChangeUserDataResponse changeUserPassword (ChangePasswordRequest request, Long userId) {
        User user = validateUserById(userId);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wrong password"
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
    public ChangeUserDataResponse changeUsername(ChangeUsernameRequest request, Long userId){

        User user = validateUserById(userId);

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
    public ChangeUserDataResponse changeEmail(ChangeEmailRequest request, Long userId) {
        User user = validateUserById(userId);

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
        verificationService.sendVerificationEmail(user);

        return toResponse(user);
    }

    @Transactional
    public ChangeUserDataResponse changeProfilePicture(ChangeProfilePictureRequest request, Long userId){

        User user = validateUserById(userId);

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
    public ChangeUserDataResponse changeCountry(ChangeCountryRequest request, Long userId){

        User user = validateUserById(userId);

        if (user.getCountryCode().equals(request.newCountryCode())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Choose new country"
            );
        }

        user.setCountryCode(request.newCountryCode());

        return toResponse(user);
    }

    @Transactional(readOnly = true)
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
    public DeleteUserResponse deleteUser (DeleteUserRequest request, Long userId){
        User user = validateUserById(userId);

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
//        User user = validateUserById(userId);
//        user.setCommends(user.getCommends() + 1);
//        return toResponse(user);
//
//    }
//
//    @Transactional
//    public ChangeUserDataResponse incrementUserMessages(){
//
//        User user = validateUserById(userId);
//        user.setMessages(user.getMessages() + 1);
//        return toResponse(user);
//    }

    private ChangeUserDataResponse toResponse(User user) {
        return new ChangeUserDataResponse(
                user.getUsername(),
                user.getEmail(),
                user.getProfilePictureUrl(),
                user.isContainsProfilePicture(),
                user.getCountryCode(),
                user.getCommends(),
                user.getMessages()
        );
    }

    public User validateUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }
}
