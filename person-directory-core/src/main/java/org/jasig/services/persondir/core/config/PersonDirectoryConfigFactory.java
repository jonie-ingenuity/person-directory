package org.jasig.services.persondir.core.config;

public final class PersonDirectoryConfigFactory {
    private PersonDirectoryConfigFactory() {
    }
    
    public static PersonDirectoryBuilder newPersonDirectoryBuilder(String primaryIdAttribute) {
        return new PersonDirectoryConfigBuilder(primaryIdAttribute);
    }
}
