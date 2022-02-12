package me.iiaii.completablefuture.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long postId;

    private Long id;

    private String email;

    private String name;

    private String body;

}
