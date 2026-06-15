package com.example.demo.leaf.dto.request;

import com.example.demo.branch.Branch;

public record EditLeafRequest(
        Branch branch,
        String commentary
) implements UniqueLeaf {
}
