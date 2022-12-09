package com.urise.webapp.storage.serializer;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.Resume;
import com.urise.webapp.sql.ConnectionFactory;
import com.urise.webapp.sql.SqlHelper;
import com.urise.webapp.storage.Storage;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlStorage implements Storage {
    public final ConnectionFactory connectionFactory;
    SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        connectionFactory = () -> DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        sqlHelper = new SqlHelper(connectionFactory);
    }

    @Override
    public void clear() {
        sqlHelper.execute("DELETE FROM resume", PreparedStatement::execute);
    }

    @Override
    public void update(Resume r) {
        sqlHelper.execute("UPDATE resume r SET full_name = (?) WHERE r.uuid=(?)", se -> {
            se.setString(1, r.getFullName());
            se.setString(2, r.getUuid());
            if (se.executeUpdate() == 0) {
                throw new NotExistStorageException(r.getUuid());
            }
            return null;
        });

    }

    @Override
    public void save(Resume r) {
        sqlHelper.execute("INSERT INTO resume (uuid, full_name) VALUES (?,?)", se -> {
            se.setString(1, r.getUuid());
            se.setString(2, r.getFullName());
            se.execute();
            return null;
        });
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute("SELECT * FROM resume r WHERE r.uuid=(?)", se -> {
            se.setString(1, uuid);
            ResultSet rs = se.executeQuery();
            if (!rs.next()) {
                throw new NotExistStorageException(uuid);
            }
            return new Resume(uuid, rs.getString("full_name"));
        });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("DELETE FROM resume r WHERE uuid = (?)", se -> {
            se.setString(1, uuid);
            if (se.executeUpdate() == 0) {
                throw new NotExistStorageException(uuid);
            }
            se.executeUpdate();
            return null;
        });
    }

    @Override
    public List<Resume> getAllSorted() {
        List<Resume> resumes = new ArrayList<>();
        sqlHelper.execute("SELECT * FROM resume r ORDER BY r.full_name, r.uuid", se -> {
            ResultSet rs = se.executeQuery();
            while (rs.next()) {
                resumes.add(new Resume(rs.getString("uuid").trim(), rs.getString("full_name").trim()));
            }
            return null;
        });
        return resumes;
    }

    @Override
    public int size() {
        return sqlHelper.execute("SELECT COUNT(*) FROM resume", (se -> {
                    ResultSet rs = se.executeQuery();
                    return rs.next() ? rs.getInt(1) : 0;
                })
        );
    }
}
