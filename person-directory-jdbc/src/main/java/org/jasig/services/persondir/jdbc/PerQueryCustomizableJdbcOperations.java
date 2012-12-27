package org.jasig.services.persondir.jdbc;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.google.common.base.Function;

public interface PerQueryCustomizableJdbcOperations {

    <T> T doWithSettings(Function<JdbcOperations, T> operation, int maxRows, int queryTimeout);
    
    <T> T doNamedWithSettings(Function<NamedParameterJdbcOperations, T> operation, int maxRows, int queryTimeout);

}