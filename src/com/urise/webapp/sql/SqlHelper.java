package com.urise.webapp.sql;

import com.urise.webapp.exception.StorageException;
import com.urise.webapp.util.ExceptionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlHelper {
    public ConnectionFactory connectionFactory;

    public SqlHelper(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public <T> T execute(String sql, SqlExecutor<T> sqlExecutor) {
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            return sqlExecutor.execute(ps);

        } catch (SQLException e) {
            throw ExceptionUtil.convertException(e);
        }
    }

    public <T> T transactionExecute(SqlTransaction<T> executor) {
        try (Connection connection = connectionFactory.getConnection()) {
            try {
                connection.setAutoCommit(false);
                T res = executor.execute(connection);
                connection.commit();
                return res;
            } catch (SQLException e) {
                connection.rollback();
                throw ExceptionUtil.convertException(e);
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }
}
