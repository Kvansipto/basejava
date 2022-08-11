package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUuidStorage extends AbstractStorage {

    private final Map<String, Resume> storage = new HashMap<>();

    @Override
    protected Object getSearchKey(String uuid) {
        return uuid;
    }

    @Override
    protected boolean isExist(Object searchKey) {
        return storage.containsKey(searchKey.toString());
    }

    @Override
    protected void doUpdate(Resume r, Object searchKey) {
        storage.replace(searchKey.toString(), r);
    }

    @Override
    protected Resume doGet(Object searchKey) {
        return storage.get(searchKey.toString());
    }

    @Override
    protected void doSave(Resume r, Object searchKey) {
        storage.put(r.getUuid(), r);
    }

    @Override
    protected void doDelete(Object searchKey) {
        storage.remove(searchKey.toString());
    }

    @Override
    protected List<Resume> doGetAllSorted() {
        return storage.values().stream().toList();
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public List<Resume> getAllSorted() {
        Comparator<Resume> resumeComparator = Comparator.comparing(Resume::getFullName).thenComparing(Resume::getUuid);
        return storage.values().stream().sorted(resumeComparator).toList();
    }

    @Override
    public int size() {
        return storage.size();
    }
}
