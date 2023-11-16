package com.intellistart.interviewplanning.security;

import com.intellistart.interviewplanning.model.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<UserDetails> loadUserByEmailAndName(String email, String name) {
        return findByUsername(email)
                .map(userDetails -> {
                    JwtUserDetails jwtUserDetails = (JwtUserDetails) userDetails;
                    jwtUserDetails.setName(name);
                    return jwtUserDetails;
                });
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                    return (UserDetails) new JwtUserDetails(username, null, passwordEncoder.encode(username), authorities);
                })
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_CANDIDATE"));
                    return (UserDetails) new JwtUserDetails(username, null, passwordEncoder.encode(username), authorities);
                }));
    }
}