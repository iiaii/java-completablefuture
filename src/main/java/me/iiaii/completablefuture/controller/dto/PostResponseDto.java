package me.iiaii.completablefuture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.iiaii.completablefuture.service.dto.CommentDto;
import me.iiaii.completablefuture.service.dto.PostDto;
import me.iiaii.completablefuture.service.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostResponseDto {

    private Long id;

    private String title;

    private String body;

    private UserResponseDto userResponseDto;

    private List<CommentResponseDto> commentResponseDtos;

    public static List<PostResponseDto> toDtos(final List<PostDto> posts, final List<UserDto> users, final List<List<CommentDto>> postComments) {
        List<PostResponseDto> results = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PostDto post = posts.get(i);
            UserDto user = users.get(i);
            List<CommentDto> comments = postComments.get(i);
            results.add(toDto(post, user, comments));
        }
        return results;
    }

    public static PostResponseDto toDto(PostDto post, UserDto user, List<CommentDto> comments) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userResponseDto(UserResponseDto.toDto(user))
                .commentResponseDtos(comments.stream()
                        .map(CommentResponseDto::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

}
