package com.urise.webapp.storage;

import org.junit.jupiter.api.Disabled;

class MapUuidStorageTest extends AbstractArrayStorageTest {

    protected MapUuidStorageTest() {
        super(new MapUuidStorage());
    }

    @Override
    @Disabled
    void saveOverflow() {
        super.saveOverflow();
    }
}
