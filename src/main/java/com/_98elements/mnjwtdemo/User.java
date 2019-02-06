package com._98elements.mnjwtdemo;

import io.micronaut.security.authentication.providers.UserState;

public class User implements UserState {

    private final String username;

    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountExpired() {
        return false;
    }

    @Override
    public boolean isAccountLocked() {
        return false;
    }

    @Override
    public boolean isPasswordExpired() {
        return false;
    }
}
