package io.github.xpakx.habitauth.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserAccountRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username " + username));
        return new User(userAccount.getUsername(), userAccount.getPassword(), userAccount.getRoles());
    }

    public UserDetails userAccountToUserDetails(UserAccount userAccount) {
        return new User(userAccount.getUsername(), userAccount.getPassword(), userAccount.getRoles());
    }
}
