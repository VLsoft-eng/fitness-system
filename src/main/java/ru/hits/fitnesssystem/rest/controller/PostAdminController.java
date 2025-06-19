package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.PostService;
import ru.hits.fitnesssystem.rest.model.CreatePostDto;
import ru.hits.fitnesssystem.rest.model.PostDto;
import ru.hits.fitnesssystem.rest.model.UpdatePostDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin-access/posts")
@RequiredArgsConstructor
public class PostAdminController {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostDto createPostDto) {
        PostDto createdPost = postService.createPost(createPostDto);
        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostDto updatePostDto
    ) {
        PostDto updatedPost = postService.updatePost(id, updatePostDto);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}