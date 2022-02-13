package me.iiaii.completablefuture.controller;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.controller.dto.PostResponseDto;
import me.iiaii.completablefuture.facade.PostFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostFacade postFacade;

    @GetMapping("/v1")
    public List<PostResponseDto> fetchTopPostsV1(@RequestParam(required = false, defaultValue = "5") int postSize,
                                               @RequestParam(required = false, defaultValue = "3") int commentSize) {
        return postFacade.fetchTopPostsV1(postSize, commentSize);
    }

    @GetMapping("/v2")
    public List<PostResponseDto> fetchTopPostsV2(@RequestParam(required = false, defaultValue = "5") int postSize,
                                               @RequestParam(required = false, defaultValue = "3") int commentSize) {
        return postFacade.fetchTopPostsV2(postSize, commentSize);
    }

}
