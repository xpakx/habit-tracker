package io.github.xpakx.habitauth.user;

import io.github.xpakx.habitauth.user.dto.AuthenticationRequest;
import io.github.xpakx.habitauth.user.dto.AuthenticationResponse;
import io.github.xpakx.habitauth.user.dto.RegistrationRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegistrationRequest request);
    AuthenticationResponse generateAuthenticationToken(AuthenticationRequest authenticationRequest);
}
