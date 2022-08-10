package com.urise.webapp.storage;

import org.junit.jupiter.api.Disabled;

class MapResumeStorageTest extends AbstractArrayStorageTest {

    protected MapResumeStorageTest() {
        super(new MapResumeStorage());
    }

    @Override
    @Disabled
    void saveOverflow() {
        super.saveOverflow();
    }
}