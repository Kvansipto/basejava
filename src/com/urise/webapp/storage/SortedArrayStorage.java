package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

import java.util.Arrays;

public class SortedArrayStorage extends AbstractArrayStorage {

    @Override
    protected int findIndex(String uuid) {
        Resume searchKey = new Resume();
        searchKey.setUuid(uuid);
        return Arrays.binarySearch(storage, 0, size, searchKey);
    }

    @Override
    protected void insert(Resume r, int index) {
        System.arraycopy(storage, (-index - 1), storage, (-index), size + index + 1);
        storage[-index - 1] = r;
    }

    @Override
    protected void shift(int index) {
        System.arraycopy(storage, index + 1, storage, index, size - index);
    }
}