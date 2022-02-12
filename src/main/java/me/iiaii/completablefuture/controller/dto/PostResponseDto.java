package me.iiaii.completablefuture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

}
