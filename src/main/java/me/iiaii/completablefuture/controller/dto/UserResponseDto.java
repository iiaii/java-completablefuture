package me.iiaii.completablefuture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.iiaii.completablefuture.service.dto.UserDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String name;

    private String email;

    private String website;

    public static UserResponseDto toDto(UserDto user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .website(user.getWebsite())
                .build();
    }

}
