package com.example.demo.root;

import com.example.demo.root.dto.request.CreateRootRequest;
import com.example.demo.root.dto.request.UpdateRootDescriptionRequest;
import com.example.demo.root.dto.response.DeleteRootResponse;
import com.example.demo.root.dto.response.GetAllRoots;
import com.example.demo.root.dto.response.RootResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class RootService {

    private final UserService userService;
    private final RootRepository rootRepository;

    public RootService(UserService userService, RootRepository rootRepository) {
        this.rootRepository = rootRepository;
        this.userService = userService;
    }

    @Transactional
    public RootResponse createRoot(CreateRootRequest request, String email) {
        User user = userService.validateUser(email);

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Verify your email first"
            );
        }

        if (rootRepository.existsByTitle(request.title())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Root with this name already exists"
            );
        }

        Root root = new Root(
                request.title(),
                request.description(),
                0,
                user
        );

        rootRepository.save(root);

        return toResponse(root);
    }

    @Transactional
    public RootResponse updateRootDescription(
            String title,
            UpdateRootDescriptionRequest request,
            String email
    ) {
        Root root = validateRootOwnership(title, email);

        root.changeDescription(request.newDescription());

        return toResponse(root);
    }

    @Transactional
    public DeleteRootResponse deleteRoot(String title, String email){
        Root root = validateRootOwnership(title, email);
        rootRepository.delete(root);

        return new DeleteRootResponse(
                "Root: " + root.getTitle() + " deleted successfully"
        );
    }

    @Transactional(readOnly = true)
    public GetAllRoots getAllRoots(){
        List<RootResponse> allRoots = rootRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        return new GetAllRoots(allRoots);
    }

    @Transactional(readOnly = true)
    public RootResponse getRoot(String title){
        Root root = rootRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Root not found"
                ));

        return toResponse(root);
    }

    private Root validateRootOwnership(String title, String email) {
        User user = userService.validateUser(email);

        Root root = rootRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Root not found"
                ));

        if (!Objects.equals(root.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No access to root"
            );
        }

        return root;
    }

    private RootResponse toResponse(Root root) {
        return new RootResponse(
                root.getTitle(),
                root.getDescription(),
                root.getActivityRating(),
                root.getUser().getUsername()
        );
    }
}

//    @Transactional
//    public RootResponse updateRootActivityRating(){
//        Root root = validateRootOwnership(request);
//
//        ///root.setActivityRating(); algorithm todo
//
//        return toResponse(root);
//    }
