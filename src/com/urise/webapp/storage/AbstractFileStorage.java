package com.urise.webapp.storage;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.model.Resume;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractFileStorage extends AbstractStorage<File> {
    private final File directory;

    protected AbstractFileStorage(File directory) {
        Objects.requireNonNull(directory, "directory must not be null");
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not directory");
        }
        if (!directory.canRead() || !directory.canWrite()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not readable/writable");
        }
        this.directory = directory;
    }

    @Override
    public void clear() {
        File[] listFiles = directory.listFiles();
        if (listFiles == null) {
            throw new StorageException("IO error", directory.getName() + " does not denote a directory");
        }
        for (File file : listFiles) doDelete(file);
    }


    @Override
    public int size() {
        int count = 0;
        File[] listFiles = directory.listFiles();
        if (listFiles == null) {
            throw new StorageException("IO error", directory.getName() + " does not denote a directory");
        }
        for (File file : listFiles) count++;
        return count;
    }

    @Override
    protected File getSearchKey(String uuid) {
        return new File(directory, uuid);
    }

    @Override
    protected void doUpdate(Resume r, File file) {
        try {
            doWrite(r, new BufferedOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            throw new StorageException("File write error", file.getName(), e);
        }
    }

    @Override
    protected boolean isExist(File file) {
        return file.exists();
    }

    @Override
    protected void doSave(Resume r, File file) {
        try {
            file.createNewFile();
            doWrite(r, new BufferedOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            throw new StorageException("IO error", file.getName(), e);
        }
    }

    @Override
    protected Resume doGet(File file) {
        try {
            return doRead(new BufferedInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            throw new StorageException("File read error", file.getName(), e);
        }
    }

    @Override
    protected void doDelete(File file) {
        file.delete();
        if (isExist(file)) {
            throw new StorageException("IO error", file.getName() + " is not deleted");
        }
    }

    @Override
    protected List<Resume> doGetAllSorted() {
        List<Resume> resumes = new ArrayList<>();
        File[] listFiles = directory.listFiles();
        if (listFiles == null) {
            throw new StorageException("IO error", directory.getName() + " does not denote a directory");
        }
        for (File file : listFiles) {
            try {
                resumes.add(doRead(new BufferedInputStream(new FileInputStream(file))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resumes;
    }

    protected abstract void doWrite(Resume r, OutputStream os) throws IOException;

    protected abstract Resume doRead(InputStream is) throws IOException;
}
