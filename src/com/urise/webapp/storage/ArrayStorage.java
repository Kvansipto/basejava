package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

import java.util.Arrays;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage {
    protected static final int STORAGE_LIMIT = 10000;
    private final Resume[] storage = new Resume[STORAGE_LIMIT];
    private int size = 0;

    public void clear() {
        Arrays.fill(storage, null);
        size = 0;
    }

    public void save(Resume r) {
        if (size + 1 > STORAGE_LIMIT) {
            System.out.println("ERROR: нет места для добавления резюме " + r.uuid);
        } else if (findIndex(r.uuid) > -1) {
            System.out.println("ERROR: резюме c uuid= " + r.uuid + " уже есть");
        } else {
            storage[size] = r;
            size++;
        }
    }

    public void update(Resume r) {
        if (findIndex(r.uuid) < 0) {
            System.out.println("ERROR: резюме c uuid= " + r.uuid + " не существует");
        } else {
            storage[findIndex(r.uuid)] = r;
            System.out.println("Resume " + r.uuid + " was updated");
        }
    }

    public Resume get(String uuid) {
        if (findIndex(uuid) < 0) {
            System.out.println("ERROR: резюме c uuid= " + uuid + " не существует");
            return null;
        } else {
            return storage[findIndex(uuid)];
        }
    }

    public void delete(String uuid) {
        int index = findIndex(uuid);
        if (index < 0) {
            System.out.println("ERROR: резюме c uuid= " + uuid + " не существует");
        } else {
            System.arraycopy(storage, index + 1, storage, index, size - index - 1);
            size--;
        }
    }

    private int findIndex(String uuid) {
        for (int i = 0; i < size; i++) {
            if (storage[i].uuid.equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    public Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    public int size() {
        return size;
    }
}