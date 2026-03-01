package com.example.CGI_Restaurant.security;

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads a user by email and returns a {@link CustomerDetails} for Spring Security authentication.
 * Throws UsernameNotFoundException when the user does not exist.
 */
@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /** Loads user by email (used as username); returns CustomerDetails or throws if not found. */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new CustomerDetails(user);
    }
}
