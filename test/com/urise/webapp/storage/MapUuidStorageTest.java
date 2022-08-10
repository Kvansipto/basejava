package com.urise.webapp.storage;

import org.junit.jupiter.api.Disabled;

class MapUuidStorageTest extends AbstractArrayStorageTest {

    protected MapUuidStorageTest() {
        super(new MapUuidStorage());
    }

    @Override
    @Disabled
    public void saveOverflow() {
        super.saveOverflow();
    }
}
