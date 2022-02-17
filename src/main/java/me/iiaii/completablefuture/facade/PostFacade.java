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
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private PostFacade self;
    private final SimpleKeyGenerator simpleKeyGenerator;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    /**
     * 기본 api 조합 후 반환
     *
     * @param postSize
     * @param commentSize
     * @return
     */
    public List<PostResponseDto> fetchTopPostsV1(final int postSize, final int commentSize) {
        List<PostDto> posts = postService.fetchPosts(postSize);
        List<UserDto> users = userService.fetchUsers(posts);
        List<List<CommentDto>> comments = commentService.fetchAllComments(posts, commentSize);
        return PostResponseDto.toDtos(posts, users, comments);
    }

    /**
     * CompletableFuture 로 api 조합 후 반환
     *
     * @param postSize
     * @param commentSize
     * @return
     */
    public List<PostResponseDto> fetchTopPostsV2(final int postSize, final int commentSize) {
        return fetchTopPostsAsync(postSize, commentSize);
    }



    /**
     * CompletableFuture + EhCache
     *
     * @param postSize
     * @param commentSize
     * @return
     */
    @Cacheable(keyGenerator = "simpleKeyGenerator")
    public List<PostResponseDto> fetchTopPostsV3(final int postSize, final int commentSize) {
        return fetchTopPostsAsync(postSize, commentSize);
    }

    /**
     * CompletableFuture + EhCache + Scheduled
     *
     * @return
     */
    @Cacheable(key = "'defaultPost'")
    public List<PostResponseDto> fetchTopPostsV4() {
        return fetchTopPostsAsync(DEFAULT_POST_SIZE, DEFAULT_COMMENT_SIZE);
    }

    @Override
    public CompletableFuture<?> poll() {
        return CompletableFuture.runAsync(self::fetchTopPostsV4);
    }

    /**
     * CompletableFuture allOf 로 api 비동기 호출 및 조합 후 반환
     *
     * @param postSize
     * @param commentSize
     * @return
     */
    private List<PostResponseDto> fetchTopPostsAsync(final int postSize, final int commentSize) {
        return getPostsAsync(postSize).thenCompose(posts -> getTopPostsAsyncAllOf(posts, commentSize))
                .exceptionally(throwable -> Collections.emptyList())
                .join();
    }

    private CompletableFuture<List<PostResponseDto>> getTopPostsAsyncAllOf(final List<PostDto> posts, final int commentSize) {
        if (posts.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        CompletableFuture<List<UserDto>> userCF = getUsersAsync(posts);
        CompletableFuture<List<List<CommentDto>>> commentsCF = getCommentsAsync(posts, commentSize);
        return CompletableFuture.allOf(userCF, commentsCF)
                .thenApply(Void -> PostResponseDto.toDtos(posts, userCF.join(), commentsCF.join()));
    }

    private CompletableFuture<List<PostDto>> getPostsAsync(final int postSize) {
        return CompletableFuture.supplyAsync(() -> postService.fetchPosts(postSize), threadPoolTaskExecutor)
                .exceptionally(throwable -> Collections.emptyList());
    }

    private CompletableFuture<List<UserDto>> getUsersAsync(final List<PostDto> posts) {
        return CompletableFuture.supplyAsync(() -> userService.fetchUsers(posts), threadPoolTaskExecutor)
                .exceptionally(throwable -> Collections.emptyList());
    }

    private CompletableFuture<List<List<CommentDto>>> getCommentsAsync(final List<PostDto> posts, final int commentSize) {
        return CompletableFuture.supplyAsync(() -> commentService.fetchAllComments(posts, commentSize), threadPoolTaskExecutor)
                .exceptionally(throwable -> Collections.emptyList());
    }

}
