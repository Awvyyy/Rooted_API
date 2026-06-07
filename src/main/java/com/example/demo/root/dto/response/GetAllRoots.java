package com.example.demo.root.dto.response;

import java.util.List;

public record GetAllRoots(
        List<RootResponse> roots
) {
}
