package me.iiaii.completablefuture.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iiaii.completablefuture.controller.dto.PostResponseDto;
import me.iiaii.completablefuture.scheduler.Pollable;
import me.iiaii.completablefuture.service.CommentService;
import me.iiaii.completablefuture.service.PostService;
import me.iiaii.completablefuture.service.UserService;
import me.iiaii.completablefuture.service.dto.CommentDto;
import me.iiaii.completablefuture.service.dto.PostDto;
import me.iiaii.completablefuture.service.dto.UserDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@CacheConfig(cacheNames = "PostFacade")
@RequiredArgsConstructor
public class PostFacade implements Pollable {

    private static final int DEFAULT_POST_SIZE = 5;
    private static final int DEFAULT_COMMENT_SIZE = 3;

    @Resource
    private PostFacade self;
    private final SimpleKeyGenerator simpleKeyGenerator;
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

    @Cacheable(keyGenerator = "simpleKeyGenerator")
    public List<PostResponseDto> fetchTopPostsV3(final int postSize, final int commentSize) {
        return fetchTopPostsV2(postSize, commentSize);
    }

    @Cacheable(key = "'defaultPost'")
    public List<PostResponseDto> fetchTopPostsV4() {
        return fetchTopPostsV2(DEFAULT_POST_SIZE, DEFAULT_COMMENT_SIZE);
    }

    @Override
    public CompletableFuture<?> poll() {
        return CompletableFuture.runAsync(self::fetchTopPostsV4);
    }

}
