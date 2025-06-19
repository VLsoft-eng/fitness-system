package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.entity.Post;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        String title,
        String description,
        String imageBase64,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostDto fromEntity(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageBase64(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}