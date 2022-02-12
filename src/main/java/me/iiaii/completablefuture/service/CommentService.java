package me.iiaii.completablefuture.service;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.config.ApiCaller;
import me.iiaii.completablefuture.service.dto.CommentDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ApiCaller apiCaller;

    public List<CommentDto> fetchComments(final Long postId, final int commentSize) {
        return Arrays.stream(apiCaller.fetch("/posts/" + postId + "/comments", CommentDto[].class))
                .limit(commentSize)
                .collect(Collectors.toList());
    }

}
