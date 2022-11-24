package com.urise.webapp.storage.serializer;

import java.io.IOException;

@FunctionalInterface
public interface DataStreamListConsumer<T> {

    T accept() throws IOException;
}
