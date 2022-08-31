package io.github.xpakx.habitauth.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthenticationResponse {
    private String token;
    private String username;
}
