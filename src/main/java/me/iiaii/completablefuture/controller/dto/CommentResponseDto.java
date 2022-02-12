package me.iiaii.completablefuture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.iiaii.completablefuture.service.dto.CommentDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentResponseDto {

    private Long postId;

    private Long id;

    private String name;

    private String body;

    private String email;

    public static CommentResponseDto toDto(CommentDto comment) {
        return CommentResponseDto.builder()
                .postId(comment.getPostId())
                .id(comment.getId())
                .name(comment.getName())
                .body(comment.getBody())
                .email(comment.getEmail())
                .build();
    }

}
