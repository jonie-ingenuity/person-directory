package org.jasig.services.persondir.jdbc;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.common.base.Function;

/**
 * @author Eric Dalquist
 */
public class CriteriaJdbcPersonSource implements CriteriaSearchableAttributeSource {
    public static final String DEFAULT_CRITERIA_PLACEHOLDER_PATTERN = "{}";
    
    private PerQueryCustomizableJdbcOperations jdbcOperations;
    private String queryTemplate;
    private ResultSetExtractor<List<PersonAttributes>> resultSetExtractor;
    private Pattern criteriaPlaceholderPattern = Pattern.compile(Pattern.quote(DEFAULT_CRITERIA_PLACEHOLDER_PATTERN));
    
    public void setCriteriaPlaceholder(String criteriaPlaceholder) {
        this.criteriaPlaceholderPattern = Pattern.compile(Pattern.quote(criteriaPlaceholder));
    }
    public void setCriteriaPlaceholderPattern(String criteriaPlaceholderPattern) {
        this.criteriaPlaceholderPattern = Pattern.compile(criteriaPlaceholderPattern);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcOperations = new PerQueryCustomizableJdbcTemplate(dataSource);
    }
    
    protected void setJdbcOperations(PerQueryCustomizableJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
    
    public void setQueryTemplate(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }
    
    public void setResultSetExtractor(
            ResultSetExtractor<List<PersonAttributes>> resultSetExtractor) {
        this.resultSetExtractor = resultSetExtractor;
    }
    
    @Override
    public Set<String> getAvailableAttributes() {
        return Collections.emptySet();
    }
    
    @Override
    public List<PersonAttributes> searchForAttributes(AttributeQuery<Criteria> query) {
        final Criteria criteria = query.getQuery();
        
        final CriteriaSqlStringBuilder criteriaStringBuilder = new CriteriaSqlStringBuilder();
        criteria.process(criteriaStringBuilder);
        
        final String sqlCriteria = criteriaStringBuilder.toString();
        final List<Object> params = criteriaStringBuilder.getParams();
        
        final Matcher queryTemplateMatcher = criteriaPlaceholderPattern.matcher(this.queryTemplate);
        final String sql = queryTemplateMatcher.replaceAll(sqlCriteria);
        
        return this.jdbcOperations.doWithSettings(new Function<JdbcOperations, List<PersonAttributes>>() {
            public List<PersonAttributes> apply(JdbcOperations jdbcOperations) {
                return jdbcOperations.query(sql, params.toArray(), resultSetExtractor);
            }
        }, 
        query.getMaxResults(), 
        query.getQueryTimeout());
    }
}
