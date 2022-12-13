package com.urise.webapp.storage;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.ContactType;
import com.urise.webapp.model.Resume;
import com.urise.webapp.sql.SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {
    private final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public void clear() {
        sqlHelper.execute("DELETE FROM resume", PreparedStatement::execute);
    }

    @Override
    public void update(Resume r) {
        sqlHelper.transactionExecute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE resume r SET full_name = (?) WHERE r.uuid=(?)")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(r.getUuid());
                }
                deleteContacts(connection, r);
                insertContact(connection, r);
                return null;
            }
        });
    }

    @Override
    public void save(Resume r) {
        sqlHelper.transactionExecute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO resume (uuid, full_name) VALUES (?,?)")) {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.execute();
                insertContact(connection, r);
            }
            return null;
        });
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute(
                """
                        SELECT * FROM resume r
                        LEFT JOIN contact c
                        ON r.uuid = c.resume_uuid
                        WHERE r.uuid=?""",
                se -> {
                    se.setString(1, uuid);
                    ResultSet rs = se.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    Resume r = new Resume(uuid, rs.getString("full_name"));
                    do {
                        addContact(rs, r);
                    } while (rs.next());
                    return r;
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
        return sqlHelper.execute("""
                SELECT * FROM resume r
                LEFT JOIN contact c
                ON r.uuid = c.resume_uuid
                ORDER BY r.full_name, r.uuid""", se -> {
            ResultSet rs = se.executeQuery();
            Map<String, Resume> map = new LinkedHashMap<>();
            while (rs.next()) {
                String uuid = rs.getString("uuid").trim();
                Resume r = map.get(uuid);
                if (r == null) {
                    r = new Resume(uuid, rs.getString("full_name").trim());
                    map.put(uuid, r);
                }
                addContact(rs, r);
            }
            return new ArrayList<>(map.values());
        });
    }

    @Override
    public int size() {
        return sqlHelper.execute("SELECT COUNT(*) FROM resume", (se -> {
                    ResultSet rs = se.executeQuery();
                    return rs.next() ? rs.getInt(1) : 0;
                })
        );
    }

    private void addContact(ResultSet rs, Resume r) throws SQLException {
        if (rs.getString("type") != null) {
            r.addContact(ContactType.valueOf(rs.getString("type")), rs.getString("value"));
        }
    }

    private void insertContact(Connection connection, Resume r) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?,?,?)")) {
            for (Map.Entry<ContactType, String> e : r.getContactMap().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, e.getKey().name());
                ps.setString(3, e.getValue());
                ps.execute();
            }
        }
    }

    private void deleteContacts(Connection connection, Resume r) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM contact\n WHERE resume_uuid = ?")) {
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }
}
