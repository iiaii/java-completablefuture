package me.iiaii.completablefuture.service;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.config.ApiCaller;
import me.iiaii.completablefuture.service.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ApiCaller apiCaller;

    public UserDto fetchUser(final Long userId) {
        return apiCaller.fetch("/users/" + userId, UserDto.class);
    }

}
