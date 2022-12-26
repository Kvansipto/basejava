package com.urise.webapp.storage;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.*;
import com.urise.webapp.sql.SqlHelper;

import java.sql.Date;
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
        return sqlHelper.transactionExecute(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    SELECT * FROM resume r
                    LEFT JOIN contact c
                    ON r.uuid = c.resume_uuid
                    LEFT JOIN sections s
                    ON r.uuid = s.resume_uuid
                    LEFT JOIN section_type st
                    ON s.section_type_id = st.id
                    WHERE r.uuid=?""");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NotExistStorageException(uuid);
            }
            Resume r = new Resume(uuid, rs.getString("full_name"));
            do {
                addContact(rs, r);
                addSection(rs, r);
            } while (rs.next());
            ps = connection.prepareStatement("""
                    SELECT * FROM resume r
                    LEFT JOIN company_sections cs on r.uuid = cs.resume_uuid
                    LEFT JOIN section_type st on cs.section_type_id = st.id
                    WHERE r.uuid=?
                    """, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, uuid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NotExistStorageException(uuid);
            }
            do {
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
        return sqlHelper.transactionExecute(conn -> {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT * FROM resume r
                    ORDER BY r.full_name, r.uuid""");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid").trim();
                Resume r = new Resume(uuid, rs.getString("full_name").trim());
                map.put(uuid, r);
            }
            ps = conn.prepareStatement("SELECT * FROM contact c");
            rs = ps.executeQuery();
            while (rs.next()) {
                addContact(rs, map.get(rs.getString("resume_uuid").trim()));
            }
            ps = conn.prepareStatement("""
                    SELECT * FROM sections s
                    LEFT JOIN section_type st
                    ON s.section_type_id = st.id
                    LEFT JOIN resume r
                    ON r.uuid = s.resume_uuid""");
            rs = ps.executeQuery();
            while (rs.next()) {
                addSection(rs, map.get(rs.getString("resume_uuid").trim()));
            }
            ps = conn.prepareStatement("""
                    SELECT * FROM company_sections
                    LEFT JOIN section_type st
                    ON company_sections.section_type_id = st.id
                    LEFT JOIN resume r
                    ON company_sections.resume_uuid = r.uuid
                    """, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = ps.executeQuery();
            while (rs.next()) {
                addSection(rs, map.get(rs.getString("resume_uuid").trim()));
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
                case EXPERIENCE, EDUCATION -> r.addSection(sectionType, new CompanySection(addCompanySection(rs, r)));
            }
        }
    }

    private List<Company> addCompanySection(ResultSet rs, Resume r) throws SQLException {
        List<Company> companies = new ArrayList<>();
        List<Company.Period> periods;
        String sectionType = rs.getString("name");
        String uuid = rs.getString("uuid").trim();
        do {
            if (!rs.getString("name").equals(sectionType)
                    || !rs.getString("uuid").trim().equals(uuid)) {
                break;
            }
            periods = new ArrayList<>();
            String companyName = rs.getString("company_name");
            Company.Period period = createPeriod(rs);
            periods.add(period);
            while (rs.next()) {
                if (rs.getString("company_name").equals(companyName)
                        && rs.getString("name").equals(sectionType)) {
                    periods.add(createPeriod(rs));
                } else {
                    rs.previous();
                    break;
                }
            }
            companies.add(new Company(companyName, periods));
        } while (rs.next());
        rs.previous();
        return companies;
    }

    private Company.Period createPeriod(ResultSet rs) throws SQLException {
        return new Company.Period(
                rs.getDate("date_from").toLocalDate(),
                rs.getDate("date_to").toLocalDate(),
                rs.getString("title"),
                rs.getString("text"));
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
        connection.setAutoCommit(false);
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO sections (section_type_id,resume_uuid,text) VALUES (?,?,?)");
        for (Map.Entry<SectionType, Section> e : r.getSectionMap().entrySet()) {
            ps.setInt(1, sectionType.get(e.getKey().name().toLowerCase(Locale.ROOT)));
            ps.setString(2, r.getUuid());
            switch (e.getKey()) {
                case PERSONAL, OBJECTIVE -> {
                    ps.setString(3, ((TextSection) e.getValue()).getContent());
                    ps.execute();
                }
                case ACHIEVEMENT, QUALIFICATIONS -> {
                    StringBuilder sb = new StringBuilder();
                    for (String s : ((ListSection) e.getValue()).getContent()) {
                        sb.append(s).append("\n");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    ps.setString(3, String.valueOf(sb));
                    ps.execute();
                }
                case EXPERIENCE, EDUCATION -> {
                    ps = connection.prepareStatement("INSERT INTO company_sections (section_type_id, resume_uuid, company_name, date_from, date_to, text,title) VALUES (?,?,?,?,?,?,?)");
                    for (Company c : ((CompanySection) e.getValue()).getCompanies()) {
                        for (Company.Period period : c.getPeriods()) {
                            ps.setInt(1, sectionType.get((e.getKey().name().toLowerCase(Locale.ROOT))));
                            ps.setString(2, r.getUuid());
                            ps.setString(3, c.getName());
                            ps.setDate(4, Date.valueOf(period.getDateBegin()));
                            ps.setDate(5, Date.valueOf(period.getDateEnd()));
                            ps.setString(6, period.getDescription());
                            ps.setString(7, period.getTitle());
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                }
            }
        }
        connection.commit();
    }


    private void deleteContacts(Connection connection, Resume r) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM contact\n WHERE resume_uuid = ?")) {
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }

    private void deleteSections(Connection connection, Resume r) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM sections\n WHERE resume_uuid = ?; DELETE FROM company_sections\n WHERE resume_uuid = ?")) {
            ps.setString(1, r.getUuid());
            ps.setString(2, r.getUuid());
            ps.execute();
        }
    }
}
