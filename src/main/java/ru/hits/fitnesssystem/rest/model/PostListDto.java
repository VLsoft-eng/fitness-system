package ru.hits.fitnesssystem.rest.model;

import java.util.List;

public record PostListDto(
        List<PostDto> posts,
        long totalElements,
        int totalPages,
        int currentPage,
        boolean isLast
) {}