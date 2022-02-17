package me.iiaii.completablefuture.service;

import lombok.RequiredArgsConstructor;
import me.iiaii.completablefuture.config.ApiCaller;
import me.iiaii.completablefuture.service.dto.PostDto;
import me.iiaii.completablefuture.service.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ApiCaller jsonPlaceHolderApiCaller;

    public UserDto fetchUser(final Long userId) {
        return jsonPlaceHolderApiCaller.fetch("/users/" + userId, UserDto.class);
    }

    public List<UserDto> fetchUsers(final List<PostDto> posts) {
        return posts.stream()
                .map(post -> fetchUser(post.getUserId()))
                .collect(Collectors.toList());
    }

}
