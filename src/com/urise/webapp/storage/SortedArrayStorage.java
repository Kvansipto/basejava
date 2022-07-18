package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

import java.util.Arrays;

public class SortedArrayStorage extends AbstractArrayStorage {

    @Override
    public void save(Resume r) {
        int index = findIndex(r.getUuid());
        if (size + 1 > STORAGE_LIMIT) {
            System.out.println("Storage overflow");
        } else if (index >= 0) {
            System.out.println("Resume " + r.getUuid() + " already exist");
        } else {
            System.arraycopy(storage, (-index - 1), storage, (-index), size + index + 1);
            storage[-index - 1] = r;
        }
        size++;
    }

    @Override
    public void delete(String uuid) {
        int index = findIndex(uuid);
        if (index < 0) {
            System.out.println("Resume " + uuid + " not exist");
        } else {
            System.arraycopy(storage, index + 1, storage, index, size - index - 1);
            size--;
        }
    }

    @Override
    protected int findIndex(String uuid) {
        Resume searchKey = new Resume();
        searchKey.setUuid(uuid);
        return Arrays.binarySearch(storage, 0, size, searchKey);
    }
}