package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

import java.util.Arrays;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage {
    Resume[] storage = new Resume[10000];
    int size = 0;

    public void clear() {
        for (int i = 0; i < size; i++) {
            storage[i] = null;
        }
        size = 0;
    }

    public void save(Resume r) {
        if (resumeExist(r.uuid)) {
            System.out.println("ERROR: резюме c uuid= " + r.uuid + " уже есть");
        } else if (size + 1 > storage.length) {
            System.out.println("ERROR: нет места для добавления резюме " + r.uuid);
        } else {
            storage[size] = r;
            size++;
        }
    }

    public void update(Resume r) {
        if (!resumeExist(r.uuid)) {
            System.out.println("ERROR: резюме c uuid= " + r.uuid + " не существует");
        } else {
            for (int i = 0; i < size; i++) {
                if (storage[i].uuid.equals(r.uuid)) {
                    storage[i] = r;
                }
            }
            System.out.println("Resume " + r.uuid + " was updated");
        }
    }

    public Resume get(String uuid) {
        if (!resumeExist(uuid)) {
            System.out.println("ERROR: резюме c uuid= " + uuid + " не существует");
            return null;
        } else {
            for (Resume resume : getAll()) {
                if (uuid.equals(resume.uuid)) {
                    return resume;
                }
            }
        }
        return null;
    }

    public void delete(String uuid) {
        if (!resumeExist(uuid)) {
            System.out.println("ERROR: резюме c uuid= " + uuid + " не существует");
        } else {
            for (int i = 0; i < size; i++) {
                if (storage[i].uuid.equals(uuid)) {
                    System.arraycopy(storage, i + 1, storage, i, size - i - 1);
                    size--;
                }
                i++;
            }
        }
    }

    private boolean resumeExist(String uuid) {
        boolean found = false;
        for (int i = 0; i < size; i++) {
            if (storage[i].uuid.equals(uuid)) {
                found = true;
                break;
            }
        }
        return found;
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