package me.iiaii.completablefuture.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.iiaii.completablefuture.config.ApiCaller;
import me.iiaii.completablefuture.service.dto.CommentDto;
import me.iiaii.completablefuture.service.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ApiCaller jsonPlaceHolderApiCaller;

    public List<CommentDto> fetchComments(final Long postId, final int commentSize) {
        return Arrays.stream(jsonPlaceHolderApiCaller.fetch("/posts/" + postId + "/comments", CommentDto[].class))
                .limit(commentSize)
                .collect(Collectors.toList());
    }

    public List<List<CommentDto>> fetchAllComments(final List<PostDto> posts, final int commentSize) {
        return posts.stream()
                .map(post -> fetchComments(post.getId(), commentSize))
                .collect(Collectors.toList());
    }

}
