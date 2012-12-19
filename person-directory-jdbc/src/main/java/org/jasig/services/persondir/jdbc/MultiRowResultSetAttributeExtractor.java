package org.jasig.services.persondir.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.PersonAttributesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * Converts each row in the result set into a {@link PersonAttributes} object
 */
public class MultiRowResultSetAttributeExtractor 
    implements ResultSetExtractor<List<PersonAttributes>> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final String mergeColumn;
    private final Map<String, Set<String>> nameValueColumnMappings;

    public MultiRowResultSetAttributeExtractor(String mergeColumn,
            Map<String, Set<String>> nameValueColumnMappings) {
        this.mergeColumn = mergeColumn;
        this.nameValueColumnMappings = nameValueColumnMappings;
    }

    public List<PersonAttributes> extractData(ResultSet rs) throws SQLException {
        final Map<String, PersonAttributesBuilder> resultsBuiler = new LinkedCaseInsensitiveMap<PersonAttributesBuilder>();
        
        final ResultSetMetaData metaData = rs.getMetaData();
        
        //Build map of column name to column index and determine which column contains the merge key
        final Map<String, Integer> columnIndexMap = new LinkedHashMap<String, Integer>();
        int mergeColumnIndex = -1;
        for (int col = 1; col <= metaData.getColumnCount(); col++) {
            final String name = JdbcUtils.lookupColumnName(metaData, col);
            columnIndexMap.put(name, col);
            if (this.mergeColumn.equalsIgnoreCase(name)) {
                mergeColumnIndex = col;
            }
        }
        
        if (mergeColumnIndex < 0) {
            throw new IllegalArgumentException("Merge Column '" + this.mergeColumn + "' does not exist in the result set with column names: " + columnIndexMap.keySet());
        }
        
        while (rs.next()) {
            //Get the merge key, generally something like the "username" that ties each row in the result set together
            final String mergeKey = (String)JdbcUtils.getResultSetValue(rs, mergeColumnIndex, String.class);
            
            //get/create the attributes builder for this merge value
            PersonAttributesBuilder builder = resultsBuiler.get(mergeKey);
            if (builder == null) {
                builder = new PersonAttributesBuilder();
                resultsBuiler.put(mergeKey, builder);
                
                builder.add(this.mergeColumn, mergeKey);
            }
            
            //Iterate over attribute name columns
            for (final Map.Entry<String, Set<String>> nameValColEntry : this.nameValueColumnMappings.entrySet()) {
                final String nameCol = nameValColEntry.getKey();
                final Integer nameColIndex = columnIndexMap.get(nameCol);
                
                //No name column in this result set, skip it with a message
                if (nameColIndex == null) {
                    //TODO better message, perhaps only log this once?
                    logger.warn("The result set did not contain attribute name column {}, it will be ignored in the result map", nameCol);
                    continue;
                }
                
                //Get the attribute name
                final String name = (String)JdbcUtils.getResultSetValue(rs, nameColIndex, String.class);
                
                //Iterate over attribute value columns
                for (final String valueCol : nameValColEntry.getValue()) {
                    final Integer valueColIndex = columnIndexMap.get(valueCol);
                    
                    //No value column in this result set, skip it with a message
                    if (valueColIndex == null) {
                        //TODO better message, perhaps only log this once?
                        logger.warn("The result set did not contain attribute value column {}, it will be ignored in the result map", nameCol);
                        continue; 
                    }
                    
                    //Get the attribute value and add it to the builder
                    final Object value = JdbcUtils.getResultSetValue(rs, valueColIndex);
                    builder.add(name, value);
                }
            }
        }
        
        //Covert the attribute builders into concrete PersonAttribute instances
        final List<PersonAttributes> results = new ArrayList<PersonAttributes>(resultsBuiler.size());
        for (final PersonAttributesBuilder builder : resultsBuiler.values()) {
            results.add(builder.build());
        }
        
        return results;
    }
}
