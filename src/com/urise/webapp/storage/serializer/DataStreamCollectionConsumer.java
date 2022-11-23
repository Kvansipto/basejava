package com.urise.webapp.storage.serializer;

import java.io.IOException;

@FunctionalInterface
public interface DataStreamCollectionConsumer<T> {

    void accept(T t) throws IOException;
}
