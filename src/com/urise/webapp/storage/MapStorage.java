package com.urise.webapp.storage;

import com.urise.webapp.model.Resume;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapStorage extends AbstractStorage {

    private final Map<String, Resume> hashmap = new HashMap<>();

    @Override
    protected Object getSearchKey(String uuid) {
        final Set<Map.Entry<String, Resume>> entries = hashmap.entrySet();
        for (Map.Entry<String, Resume> entry : entries) {
            if (entry.getKey().equals(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    protected boolean isExist(Object searchKey) {
        return searchKey != null;
    }

    @Override
    protected void doUpdate(Resume r, Object searchKey) {
        hashmap.put(r.getUuid(), r);
    }

    @Override
    protected Resume doGet(Object searchKey) {
        return hashmap.get(searchKey.toString());
    }

    @Override
    protected void doSave(Resume r, Object searchKey) {
        hashmap.put(r.getUuid(), r);
    }

    @Override
    protected void doDelete(Object searchKey) {
        hashmap.remove(searchKey.toString());
    }

    @Override
    public void clear() {
        hashmap.clear();
    }

    @Override
    public Resume[] getAll() {
        return hashmap.values().toArray(new Resume[0]);
    }

    @Override
    public int size() {
        return hashmap.size();
    }
}
