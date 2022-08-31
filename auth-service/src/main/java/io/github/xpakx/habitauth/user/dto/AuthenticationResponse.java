package io.github.xpakx.habitauth.user.dto;

import lombok.Builder;

@Builder
public class AuthenticationResponse {
    private String token;
    private String username;
}
