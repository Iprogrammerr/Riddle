package com.iprogrammerr.riddle.model;

import java.util.List;

import com.iprogrammerr.bright.server.model.KeyValue;

public interface Record {

    Record put(String key, Object value);

    List<KeyValue> columns();

    String name();
}
