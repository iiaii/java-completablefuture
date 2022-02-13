package me.iiaii.completablefuture.facade;

import me.iiaii.completablefuture.controller.dto.PostResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostFacadeTest {

    @Autowired
    private PostFacade postFacade;

    @Test
    @DisplayName("포스트 요청 - 기본")
    public void 포스트요청_기본() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postFacade.fetchTopPostsV1(postSize, commentSize);

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

    @Test
    @DisplayName("포스트 요청 - CompletableFuture 사용")
    public void 포스트요청_CompletableFuture사용() {
        // given
        int postSize = 5;
        int commentSize = 3;

        // when
        List<PostResponseDto> postResponseDtos = postFacade.fetchTopPostsV1(postSize, commentSize);

        // then
        assertThat(postResponseDtos.size()).isEqualTo(postSize);
        assertThat(postResponseDtos).allMatch(post -> post.getCommentResponseDtos().size() == commentSize);
    }

}