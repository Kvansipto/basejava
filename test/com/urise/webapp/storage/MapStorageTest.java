package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;
import org.junit.jupiter.api.Disabled;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MapStorageTest extends AbstractArrayStorageTest {

    protected MapStorageTest() {
        super(new MapStorage());
    }

    @Override
    @Disabled
    void storageOverflow() {
        super.storageOverflow();
    }

    @Override
    public void arrayEquals(Resume[] expected, Resume[] actual) {
        assertArrayEquals(expected, Arrays.stream(actual).sorted().toArray());
    }
}