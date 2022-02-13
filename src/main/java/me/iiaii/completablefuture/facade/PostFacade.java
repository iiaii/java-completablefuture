package me.iiaii.completablefuture.facade;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.controller.dto.PostResponseDto;
import me.iiaii.completablefuture.service.CommentService;
import me.iiaii.completablefuture.service.PostService;
import me.iiaii.completablefuture.service.UserService;
import me.iiaii.completablefuture.service.dto.CommentDto;
import me.iiaii.completablefuture.service.dto.PostDto;
import me.iiaii.completablefuture.service.dto.UserDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class PostFacade {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    public List<PostResponseDto> fetchTopPostsV1(final int postSize, final int commentSize) {
        List<PostDto> posts = postService.fetchPosts(postSize);
        List<UserDto> users = userService.fetchUsers(posts);
        List<List<CommentDto>> comments = commentService.fetchAllComments(posts, commentSize);
        return PostResponseDto.toDtos(posts, users, comments);
    }

    public List<PostResponseDto> fetchTopPostsV2(final int postSize, final int commentSize) {
        List<PostDto> posts = postService.fetchPosts(postSize);
        CompletableFuture<List<UserDto>> usersCF = CompletableFuture.supplyAsync(() -> userService.fetchUsers(posts))
                .exceptionally(throwable -> Collections.emptyList());
        CompletableFuture<List<List<CommentDto>>> commentsCF = CompletableFuture.supplyAsync(() -> commentService.fetchAllComments(posts, commentSize))
                .exceptionally(throwable -> Collections.emptyList());

        return CompletableFuture.allOf(usersCF, commentsCF)
                .thenApply(Void -> {
                    List<UserDto> users = usersCF.join();
                    List<List<CommentDto>> comments = commentsCF.join();
                    return PostResponseDto.toDtos(posts, users, comments);
                })
                .join();
    }

    @Cacheable(value = "fetchTopPostsV3", key = " #postSize + ':' + #commentSize")
    public List<PostResponseDto> fetchTopPostsV3(final int postSize, final int commentSize) {
        return fetchTopPostsV2(postSize, commentSize);
    }

}
