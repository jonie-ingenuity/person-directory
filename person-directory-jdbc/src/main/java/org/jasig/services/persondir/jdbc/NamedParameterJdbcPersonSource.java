package org.jasig.services.persondir.jdbc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.google.common.base.Function;

/**
 * @author Eric Dalquist
 */
public class NamedParameterJdbcPersonSource implements SimpleSearchableAttributeSource {
    private PerQueryCustomizableJdbcOperations jdbcOperations;
    private String queryTemplate;
    private ResultSetExtractor<List<PersonAttributes>> resultSetExtractor;
    
    public void setDataSource(DataSource dataSource) {
        this.jdbcOperations = new PerQueryCustomizableJdbcTemplate(dataSource);
    }
    
    protected void setJdbcOperations(PerQueryCustomizableJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void setQueryTemplate(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }
    
    public void setResultSetExtractor(ResultSetExtractor<List<PersonAttributes>> resultSetExtractor) {
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public Set<String> getAvailableAttributes() {
        return Collections.emptySet();
    }
    
    @Override
    public List<PersonAttributes> searchForAttributes(AttributeQuery<Map<String, Object>> query) {
        final Map<String, Object> searchAttributes = query.getQuery();
        
        return this.jdbcOperations.doNamedWithSettings(new Function<NamedParameterJdbcOperations, List<PersonAttributes>>() {
            public List<PersonAttributes> apply(NamedParameterJdbcOperations jdbcOperations) {
                return jdbcOperations.query(queryTemplate, searchAttributes, resultSetExtractor);
            }
        }, 
        query.getMaxResults(), 
        query.getQueryTimeout());
    }
}
