package com.iprogrammerr.riddle.users;

import com.iprogrammerr.riddle.user.User;

public interface Users {

    Iterable<User> all();

    Iterable<User> active();

    User user(String nameOrEmail) throws Exception;

    User user(long id) throws Exception;

    boolean exists(String nameOrEmail);

    long createPlayer(String name, String email, String password) throws Exception;
}
