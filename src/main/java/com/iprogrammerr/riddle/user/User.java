package com.iprogrammerr.riddle.user;

import com.iprogrammerr.bright.server.model.KeyValue;
import com.iprogrammerr.bright.server.model.KeysValues;

public interface User {

    long id();

    String name() throws Exception;

    String email() throws Exception;

    String password() throws Exception;

    boolean active() throws Exception;

    String role() throws Exception;

    void change(KeysValues keyValues) throws Exception;

    void change(KeyValue keyValue) throws Exception;
}
