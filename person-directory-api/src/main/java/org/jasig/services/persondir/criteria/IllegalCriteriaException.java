package org.jasig.services.persondir.criteria;

/**
 * Thrown when the criteria cannot be converted into a valid query.
 * 
 * @author Eric Dalquist
 */
public class IllegalCriteriaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IllegalCriteriaException() {
        super();
    }

    public IllegalCriteriaException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCriteriaException(String message) {
        super(message);
    }

    public IllegalCriteriaException(Throwable cause) {
        super(cause);
    }

}
