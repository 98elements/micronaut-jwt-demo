package com._98elements.mnjwtdemo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.security.authentication.providers.AuthoritiesFetcher;
import io.micronaut.security.authentication.providers.PasswordEncoder;
import io.micronaut.security.authentication.providers.UserFetcher;
import io.micronaut.security.authentication.providers.UserState;
import io.reactivex.Flowable;

import javax.inject.Singleton;
import java.util.List;

@Factory
public class SecurityConfiguration {

    private final UserRepository userRepository;

    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    @Singleton
    UserFetcher userFetcher() {
        return username -> userRepository.findByUsername(username)
                .map(Flowable::just)
                .orElseGet(Flowable::empty)
                .cast(UserState.class);
    }

    // We're not using roles, so let's just return an empty list.
    @Bean
    @Singleton
    AuthoritiesFetcher authoritiesFetcher() {
        return username -> Flowable.just(List.of());
    }

    // No-op implementation for the sake of example.
    // Be sure to use a strong hashing algorithm in real life (e.g. bcrypt).
    @Bean
    @Singleton
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return rawPassword;
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return true;
            }
        };
    }

}
