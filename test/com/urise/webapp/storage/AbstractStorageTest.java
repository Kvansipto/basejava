package com.urise.webapp.storage;

import com.urise.webapp.ResumeTestData;
import com.urise.webapp.exception.ExistStorageException;
import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.Resume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractStorageTest {

    protected static final File STORAGE_DIR = new File("/Users/sheldor/IdeaProjects/basejava/storage");

    private static final String UUID_1 = "uuid1";
    private static final String NAME_1 = "JohnD";
    private static final Resume RESUME_1 = ResumeTestData.createResume(UUID_1, NAME_1);
    private static final String UUID_2 = "uuid2";
    private static final String NAME_2 = "JohnA";
    private static final Resume RESUME_2 = ResumeTestData.createResume(UUID_2, NAME_2);
    private static final String UUID_3 = "uuid3";
    private static final String NAME_3 = "JohnB";
    private static final Resume RESUME_3 = ResumeTestData.createResume(UUID_3, NAME_3);
    private static final String NAME_4 = "JohnC";
    private static final String UUID_4 = "uuid4";
    private static final Resume RESUME_4 = ResumeTestData.createResume(UUID_4, NAME_4);
    private static final String UUID_NOT_EXIST = "dummy";
    private final Storage storage;

    protected AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    void setUp() {
        storage.clear();
        storage.save(RESUME_1);
        storage.save(RESUME_2);
        storage.save(RESUME_3);
    }

    @Test
    void clear() {
        storage.clear();
        Assertions.assertIterableEquals(Arrays.stream(new Resume[0]).toList(), storage.getAllSorted());
        assertSize(0);
    }

    @Test
    public void update() {
        Resume newResume = new Resume(UUID_3, "newName");
        storage.update(newResume);
        assertEquals(newResume, storage.get(UUID_3));
    }

    @Test
    public void save() {
        List<Resume> expected = Arrays.stream(new Resume[]{RESUME_2, RESUME_3, RESUME_4, RESUME_1}).toList();
        storage.save(RESUME_4);
        assertSize(4);
        assertGet(RESUME_4);
        assertIterableEquals(expected, storage.getAllSorted());
    }

    @Test
    public void get() {
        assertGet(RESUME_1);
        assertGet(RESUME_2);
        assertGet(RESUME_3);
    }

    @Test
    public void delete() {
        storage.delete(UUID_2);
        assertSize(2);
        assertThrows(NotExistStorageException.class, () -> storage.get(UUID_2));
    }

    @Test
    public void getAll() {
        List<Resume> expected = Arrays.stream(new Resume[]{RESUME_2, RESUME_3, RESUME_1}).toList();
        List<Resume> actual = storage.getAllSorted();
        assertEquals(3, actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    public void size() {
        assertSize(3);
    }

    @Test
    public void getNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.get(UUID_NOT_EXIST));
    }

    @Test
    public void saveExist() {
        assertThrows(ExistStorageException.class, () -> storage.save(RESUME_1));
    }

    @Test
    public void updateNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.update(RESUME_4));
    }

    @Test
    public void deleteNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.delete(UUID_NOT_EXIST));
    }


    private void assertGet(Resume r) {
        assertEquals(r, storage.get(r.getUuid()));
    }

    private void assertSize(int size) {
        assertEquals(size, storage.size());
    }
}