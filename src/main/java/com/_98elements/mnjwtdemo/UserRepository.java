package com._98elements.mnjwtdemo;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class UserRepository {

    private final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

    public User create(String username, String password) {
        var user = new User(username, password);
        users.put(username, user);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

}
