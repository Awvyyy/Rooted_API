package com.example.demo.leaf.dto.request;

import jakarta.validation.constraints.NotNull;

public record DeleteLeafRequest(
        @NotNull(message = "Branch id is required")
        Long branchId
) {
}
