package me.iiaii.completablefuture.service;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.config.ApiCaller;
import me.iiaii.completablefuture.service.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ApiCaller apiCaller;

    public List<PostDto> fetchPosts(final int postSize) {
        return Arrays.stream(apiCaller.fetch("/posts", PostDto[].class))
                .limit(postSize)
                .collect(Collectors.toList());
    }

}
