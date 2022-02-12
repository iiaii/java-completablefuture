package me.iiaii.completablefuture.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.iiaii.completablefuture.controller.dto.UserResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String username;

    private String email;

    private String phone;

    private String website;

}
