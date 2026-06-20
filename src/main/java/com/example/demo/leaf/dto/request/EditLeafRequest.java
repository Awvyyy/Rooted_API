package com.example.demo.leaf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditLeafRequest(
        @NotNull(message = "Branch id is required")
        Long branchId,

        @NotBlank(message = "Commentary is required")
        @Size(max = 10_000, message = "Commentary must be at most 10000 characters")
        String commentary
) implements UniqueLeaf {
}
