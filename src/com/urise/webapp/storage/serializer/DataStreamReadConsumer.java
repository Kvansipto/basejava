package com.urise.webapp.storage.serializer;

import java.io.IOException;

@FunctionalInterface
public interface DataStreamReadConsumer {

    void accept() throws IOException;
}
