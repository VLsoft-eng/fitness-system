package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.hits.fitnesssystem.core.entity.Post;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.PostRepository;
import ru.hits.fitnesssystem.rest.model.CreatePostDto;
import ru.hits.fitnesssystem.rest.model.PostDto;
import ru.hits.fitnesssystem.rest.model.PostListDto;
import ru.hits.fitnesssystem.rest.model.UpdatePostDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пост с ID " + id + " не найден"));
        return PostDto.fromEntity(post);
    }

    public PostListDto getAllPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        List<PostDto> postDtos = postPage.getContent().stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());

        return new PostListDto(
                postDtos,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.getNumber(),
                postPage.isLast()
        );
    }

    public PostDto createPost(CreatePostDto createPostDto) {
        Post post = Post.builder()
                .title(createPostDto.title())
                .description(createPostDto.description())
                .imageBase64(createPostDto.imageBase64())
                .build();
        post = postRepository.save(post);
        return PostDto.fromEntity(post);
    }

    public PostDto updatePost(Long id, UpdatePostDto updatePostDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пост с ID " + id + " не найден"));

        if (updatePostDto.title() != null) {
            existingPost.setTitle(updatePostDto.title());
        }
        if (updatePostDto.description() != null) {
            existingPost.setDescription(updatePostDto.description());
        }
        if (updatePostDto.imageBase64() != null) {
            existingPost.setImageBase64(updatePostDto.imageBase64());
        }

        Post updatedPost = postRepository.save(existingPost);
        return PostDto.fromEntity(updatedPost);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new NotFoundException("Пост с ID " + id + " не найден");
        }
        postRepository.deleteById(id);
    }
}