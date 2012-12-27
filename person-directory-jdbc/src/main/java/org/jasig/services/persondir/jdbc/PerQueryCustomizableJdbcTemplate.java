package org.jasig.services.persondir.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.google.common.base.Function;

/**
 * Extension of {@link JdbcTemplate} that allows for per-query setting of maxRows and queryTimeout
 * via {@link #doWithSettings(Function, int, int)}
 * 
 * @author Eric Dalquist
 */
public class PerQueryCustomizableJdbcTemplate extends JdbcTemplate implements PerQueryCustomizableJdbcOperations {
    private final ThreadLocal<Integer> maxRowsLocal = new ThreadLocal<Integer>();
    private final ThreadLocal<Integer> queryTimeoutLocal = new ThreadLocal<Integer>();
    
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    
    public PerQueryCustomizableJdbcTemplate() {
        super();
        namedParameterJdbcOperations = new NamedParameterJdbcTemplate(this);
    }

    public PerQueryCustomizableJdbcTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
        namedParameterJdbcOperations = new NamedParameterJdbcTemplate(this);
    }

    public PerQueryCustomizableJdbcTemplate(DataSource dataSource) {
        super(dataSource);
        namedParameterJdbcOperations = new NamedParameterJdbcTemplate(this);
    }

    @Override
    protected void applyStatementSettings(Statement stmt) throws SQLException {
        super.applyStatementSettings(stmt);
        
        final Integer maxRows = maxRowsLocal.get();
        if (maxRows != null && maxRows > 0) {
            stmt.setMaxRows(maxRows);
        }
        
        final Integer queryTimeout = queryTimeoutLocal.get();
        if (queryTimeout != null && queryTimeout > 0) {
            DataSourceUtils.applyTimeout(stmt, getDataSource(), queryTimeout);
        }
    }
    
    @Override
    public <T> T doWithSettings(Function<JdbcOperations, T> operation, int maxRows, int queryTimeout) {
        try {
            maxRowsLocal.set(maxRows);
            queryTimeoutLocal.set(queryTimeout);
            
            return operation.apply(this);
        }
        finally {
            maxRowsLocal.remove();
            queryTimeoutLocal.remove();
        }
    }
    
    @Override
    public <T> T doNamedWithSettings(Function<NamedParameterJdbcOperations, T> operation, int maxRows, int queryTimeout) {
        try {
            maxRowsLocal.set(maxRows);
            queryTimeoutLocal.set(queryTimeout);
            
            return operation.apply(namedParameterJdbcOperations);
        }
        finally {
            maxRowsLocal.remove();
            queryTimeoutLocal.remove();
        }
    }
}
