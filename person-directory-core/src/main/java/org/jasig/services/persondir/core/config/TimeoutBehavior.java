package org.jasig.services.persondir.core.config;

/**
 * Action taken when an attribute query against an attribute source times out.
 * 
 * @author Eric Dalquist
 */
public enum TimeoutBehavior {
    /**
     * The entire attribute query fails with a TODO
     */
    FAIL,
    /**
     * A warning will be logged for the attribute query and source but the overall query will proceed as if the source returned no attributes
     */
    WARN,
    /**
     * The overall query will proceed as if the source returned no attributes
     */
    IGNORE;
}
