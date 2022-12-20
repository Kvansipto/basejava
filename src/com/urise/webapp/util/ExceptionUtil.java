package com.urise.webapp.util;

import com.urise.webapp.exception.ExistStorageException;
import com.urise.webapp.exception.StorageException;

import java.sql.SQLException;

public class ExceptionUtil {
    public static StorageException convertException(SQLException e) {
        if (e.getSQLState().equals("23505")) {
            throw new ExistStorageException(null);
        }
        throw new StorageException(e);
    }
}
