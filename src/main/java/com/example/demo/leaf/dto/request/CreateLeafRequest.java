package com.example.demo.leaf.dto.request;

import com.example.demo.branch.Branch;

public record CreateLeafRequest(
        Long branchId,
        String commentary
) implements UniqueLeaf {
}
