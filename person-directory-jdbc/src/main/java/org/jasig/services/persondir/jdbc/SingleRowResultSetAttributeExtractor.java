package org.jasig.services.persondir.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.util.attributes.ImmutablePersonAttributesImpl;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/**
 * Converts each row in the result set into a {@link PersonAttributes} object
 */
public class SingleRowResultSetAttributeExtractor 
    implements ResultSetExtractor<List<PersonAttributes>> {

    private final RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();

    public List<PersonAttributes> extractData(ResultSet rs) throws SQLException {
        final List<PersonAttributes> results = new ArrayList<PersonAttributes>();
        
        for (int rowNum = 0; rs.next(); rowNum++) {
            final Map<String, Object> rowMap = this.rowMapper.mapRow(rs, rowNum);
            results.add(ImmutablePersonAttributesImpl.create(rowMap));
        }
        
        return results;
    }

}
