package com.sjy.LitHub.global.security.service;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmailActive(userEmail)
                .orElseThrow(() -> new InvalidAuthenticationException(BaseResponseStatus.USER_NOT_FOUND));

        return new UserPrincipal(user.getId(), user.getPassword(), user.getRole());
    }
}