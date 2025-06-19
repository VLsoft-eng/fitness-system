package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.PostService;
import ru.hits.fitnesssystem.rest.model.PostDto;
import ru.hits.fitnesssystem.rest.model.PostListDto;

@RestController
@RequestMapping("/public/posts")
@RequiredArgsConstructor
public class PostPublicController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        PostDto postDto = postService.getPost(id);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping
    public ResponseEntity<PostListDto> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        PostListDto postListDto = postService.getAllPosts(pageable);
        return ResponseEntity.ok(postListDto);
    }
}