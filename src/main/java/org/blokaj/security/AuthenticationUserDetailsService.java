package org.blokaj.security;

import lombok.RequiredArgsConstructor;
import org.blokaj.exceptions.AuthenticationUserNotFoundException;
import org.blokaj.models.entities.User;
import org.blokaj.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public AuthenticationUser loadUserByUsername(String username) {

        User user = userRepository.findUserByEmailOrUsername(username).orElse(null);
        if(ObjectUtils.isEmpty(user)) {
            throw new AuthenticationUserNotFoundException("User not found");
        }

        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().getName());

        return new AuthenticationUser(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
                true
        );
    }

}