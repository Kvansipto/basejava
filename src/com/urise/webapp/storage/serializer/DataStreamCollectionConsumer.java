package com.urise.webapp.storage.serializer;

import java.io.IOException;

@FunctionalInterface
public interface DataStreamCollectionConsumer<T> {

    void write(T t) throws IOException;
}
