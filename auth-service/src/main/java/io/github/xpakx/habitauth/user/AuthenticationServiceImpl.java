package io.github.xpakx.habitauth.user;

import io.github.xpakx.habitauth.security.JwtUtils;
import io.github.xpakx.habitauth.user.dto.AuthenticationRequest;
import io.github.xpakx.habitauth.user.dto.AuthenticationResponse;
import io.github.xpakx.habitauth.user.dto.RegistrationRequest;
import io.github.xpakx.habitauth.user.error.AuthenticationException;
import io.github.xpakx.habitauth.user.error.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserAccountRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegistrationRequest request) {
        testRegistrationRequest(request);
        UserAccount userToAdd = createNewUser(request);
        authenticate(request.getUsername(), request.getPassword());
        final String token = jwtUtils.generateToken(userService.userAccountToUserDetails(userToAdd));
        return AuthenticationResponse.builder()
                .token(token)
                .username(userToAdd.getUsername())
                .build();
    }

    private void testRegistrationRequest(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Username exists!");
        }
        if (!request.getPassword().equals(request.getPasswordRe())) {
            throw new ValidationException("Passwords don't match!");
        }
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User " +username+" disabled!");
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid password!");
        }
    }

    private UserAccount createNewUser(RegistrationRequest request) {
        Set<UserRole> roles = new HashSet<>();
        UserAccount userToAdd = new UserAccount();
        userToAdd.setPassword(passwordEncoder.encode(request.getPassword()));
        userToAdd.setUsername(request.getUsername());
        userToAdd.setRoles(roles);
        return userRepository.save(userToAdd);
    }

    @Override
    public AuthenticationResponse generateAuthenticationToken(AuthenticationRequest authenticationRequest) {
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final String token = jwtUtils.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .build();
    }
}
