package org.jasig.services.persondir.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.jasig.services.persondir.criteria.LogicCriteriaBuilder;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder used to compile attributes from multiple sources
 * 
 * @author Eric Dalquist
 */
public class PersonBuilder {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Map<String, List<Object>> attributes = new LinkedHashMap<String, List<Object>>();
    private final Map<String, AttributeSourceConfig<? extends BaseAttributeSource>> attributeSources = new HashMap<String, AttributeSourceConfig<? extends BaseAttributeSource>>();
    private final LogicCriteriaBuilder subqueryCriteria = CriteriaBuilder.andBuilder();
    private final String primaryId;
    
    public PersonBuilder(String primaryId) {
        this.primaryId = primaryId;
    }

    public boolean mergeAttributes(PersonAttributes personAttributes, AttributeSourceConfig<? extends BaseAttributeSource> sourceConfig) {
        boolean changed = false;
        for (final Map.Entry<String, List<Object>> attributeEntry : personAttributes.getAttributes().entrySet()) {
            final String attribute = attributeEntry.getKey();
            final List<Object> newValues = attributeEntry.getValue();
            
            List<Object> existingValues = attributes.get(attribute);
            if (existingValues == null) {
                //Add attribute to builder map 
                attributes.put(attribute, newValues);
                
                //Track which source the attribute is from
                attributeSources.put(attribute, sourceConfig);
                
                //Add to criteria structure
                if (newValues.size() == 1) {
                    subqueryCriteria.add(CriteriaBuilder.eq(attribute, newValues.get(0)));
                }
                else {
                    final LogicCriteriaBuilder orBuilder = CriteriaBuilder.orBuilder();
                    for (final Object value : newValues) {
                        orBuilder.add(CriteriaBuilder.eq(attribute, value));
                    }
                    subqueryCriteria.add(orBuilder.build());
                }
                
                //mark that the attributes have changed
                changed = true;
            }
            else if (!existingValues.equals(newValues)) {
                final AttributeSourceConfig<? extends BaseAttributeSource> originalSourceConfig = attributeSources.get(attribute);
                logger.warn("Values for '{}' do not match for user '{}' between source '{}' and '{}'. The values from the second source will be ignored.", 
                        attribute, primaryId, originalSourceConfig.getName(), sourceConfig.getName());
            }
        }
        return changed;
    }
    
    public AttributeSourceConfig<? extends BaseAttributeSource> getAttributeSource(String attribute) {
        return this.attributeSources.get(attribute);
    }
    
    public Criteria getSubqueryCriteria() {
        return subqueryCriteria.build();
    }
}
