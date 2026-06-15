package com.example.demo.leaf.dto.response;

public record LeafResponse(
        String authorName,
        String branchName,
        String commentary,
        Integer rating
) {
}
