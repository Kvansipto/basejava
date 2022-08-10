package com.urise.webapp.storage;

import org.junit.jupiter.api.Disabled;

class ListStorageTest extends AbstractArrayStorageTest {

    public ListStorageTest() {
        super(new ListStorage());
    }

    @Override
    @Disabled
    public void saveOverflow() {
        super.saveOverflow();
    }
}