package com.urise.webapp.storage;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.*;
import com.urise.webapp.sql.SqlHelper;

import java.sql.*;
import java.util.*;

public class SqlStorage implements Storage {
    private final SqlHelper sqlHelper;
    private final Map<String, Integer> sectionType = new HashMap<>();

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));

        sqlHelper.execute("SELECT * FROM section_type", se -> {
            ResultSet resultSet = se.executeQuery();
            while (resultSet.next()) {
                sectionType.put(resultSet.getString("name"), resultSet.getInt("id"));
            }
            return null;
        });
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
                deleteSections(connection, r);
                insertContact(connection, r);
                insertSection(connection, r);
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
                insertSection(connection, r);
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
                        LEFT JOIN sections s
                        ON r.uuid = s.resume_uuid
                        LEFT JOIN section_type st
                        ON s.section_type_id = st.id
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
                        addSection(rs, r);
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
        Map<String, Resume> map = new LinkedHashMap<>();
        sqlHelper.execute("""
                SELECT * FROM resume r
                ORDER BY r.full_name, r.uuid""", se -> {
            ResultSet rs = se.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid").trim();
                Resume r = new Resume(uuid, rs.getString("full_name").trim());
                map.put(uuid, r);
            }
            return null;
        });
        sqlHelper.execute("SELECT * FROM contact c", se -> {
            ResultSet rs = se.executeQuery();
            while (rs.next()) {
                addContact(rs, map.get(rs.getString("resume_uuid").trim()));
            }
            return null;
        });
        sqlHelper.execute("""
                SELECT * FROM sections s
                LEFT JOIN section_type st
                ON s.section_type_id = st.id
                LEFT JOIN resume r
                ON r.uuid = s.resume_uuid""", se -> {
            ResultSet rs = se.executeQuery();
            while (rs.next()) {
                addSection(rs, map.get(rs.getString("resume_uuid").trim()));
            }
            return null;
        });
        return new ArrayList<>(map.values());
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
        String value = rs.getString("value");
        if (value != null) {
            r.addContact(ContactType.valueOf(rs.getString("type")), value);
        }
    }

    private void addSection(ResultSet rs, Resume r) throws SQLException {
        String name = rs.getString("name");
        if (name != null) {
            SectionType sectionType = SectionType.valueOf(name.toUpperCase(Locale.ROOT));
            String content = rs.getString("text");
            switch (sectionType) {
                case PERSONAL, OBJECTIVE -> r.addSection(sectionType, new TextSection(content));
                case ACHIEVEMENT, QUALIFICATIONS -> r.addSection(sectionType, new ListSection(Arrays.stream(content.split("\n")).toList()));
            }
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

    private void insertSection(Connection connection, Resume r) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO sections (section_type_id,resume_uuid,text) VALUES (?,?,?)")) {
            for (Map.Entry<SectionType, Section> e : r.getSectionMap().entrySet()) {
                ps.setInt(1, sectionType.get(e.getKey().name().toLowerCase(Locale.ROOT)));
                ps.setString(2, r.getUuid());
                switch (e.getKey()) {
                    case PERSONAL, OBJECTIVE -> ps.setString(3, ((TextSection) e.getValue()).getContent());
                    case ACHIEVEMENT, QUALIFICATIONS -> {
                        StringBuilder sb = new StringBuilder();
                        for (String s : ((ListSection) e.getValue()).getContent()) {
                            sb.append(s).append("\n");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        ps.setString(3, String.valueOf(sb));
                    }
                }
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

    private void deleteSections(Connection connection, Resume r) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM sections\n WHERE resume_uuid = ?")) {
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }
}
