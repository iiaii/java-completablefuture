package me.iiaii.completablefuture.controller;

import me.iiaii.completablefuture.controller.dto.PostResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostControllerTest {

    @Autowired
    private PostController postController;

    @Test
    @Order(1)
    @DisplayName("포스트 요청 - 기본 (v1)")
    public void 포스트요청_기본() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postController.fetchTopPostsV1(postSize, commentSize);

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

    @Test
    @Order(2)
    @DisplayName("포스트 요청 - CompletableFuture 사용 (v2)")
    public void 포스트요청_CompletableFuture() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postController.fetchTopPostsV2(postSize, commentSize);

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

    @Test
    @Order(3)
    @DisplayName("포스트 요청 - CompletableFuture + EhCache 사용 (v3)")
    public void 포스트요청_CompletableFuture와EhCache() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postController.fetchTopPostsV3(postSize, commentSize);

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

    @Test
    @Order(4)
    @DisplayName("포스트 요청 - CompletableFuture + EhCache 사용 재요청 (v3)")
    public void 포스트요청_CompletableFuture와EhCache_재요청() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postController.fetchTopPostsV3(postSize, commentSize);

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

    @Test
    @Order(5)
    @DisplayName("포스트 요청 - CompletableFuture + EhCache + Scheduled 사용 (v4)")
    public void 포스트요청_CompletableFuture와EhCache와Scheduled() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postController.fetchTopPostsV4();

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

}