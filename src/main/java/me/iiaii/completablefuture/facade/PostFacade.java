package me.iiaii.completablefuture.facade;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.controller.dto.CommentResponseDto;
import me.iiaii.completablefuture.controller.dto.PostResponseDto;
import me.iiaii.completablefuture.controller.dto.UserResponseDto;
import me.iiaii.completablefuture.service.CommentService;
import me.iiaii.completablefuture.service.PostService;
import me.iiaii.completablefuture.service.UserService;
import me.iiaii.completablefuture.service.dto.CommentDto;
import me.iiaii.completablefuture.service.dto.PostDto;
import me.iiaii.completablefuture.service.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostFacade {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    public List<PostResponseDto> fetchTopPosts(final int postSize, final int commentSize) {
        List<PostDto> posts = postService.fetchPosts(postSize);

        List<UserDto> users = posts.stream()
                .map(post -> userService.fetchUser(post.getUserId()))
                .collect(Collectors.toList());


        List<List<CommentDto>> postComments = posts.stream()
                .map(post -> commentService.fetchComments(post.getId(), commentSize))
                .collect(Collectors.toList());

        List<PostResponseDto> results = new ArrayList<>();

        for (int i = 0; i < postSize; i++) {
            PostDto post = posts.get(i);
            UserDto user = users.get(i);
            List<CommentDto> comments = postComments.get(i);

            results.add(PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .body(post.getBody())
                    .commentResponseDtos(comments.stream()
                            .map(CommentResponseDto::toDto)
                            .collect(Collectors.toList()))
                    .userResponseDto(UserResponseDto.toDto(user))
                    .build());
        }

        return results;
    }

}
