package me.iiaii.completablefuture.service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long userId;

    private Long id;

    private String title;

    private String body;

}
