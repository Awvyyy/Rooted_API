package com.example.demo.branch.dto.request;

public record CreateBranchRequest(
        String title,
        String description,
        String rootTitle,
        String tags,
        String photoOriginalUrl
) {

}
