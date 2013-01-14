package org.jasig.services.persondir.core;

import java.util.Comparator;

import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.gate.AttributeSourceGate;

class AttributeQueryWorkerComparator implements Comparator<AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource, ? extends AttributeSourceGate>, ? extends AttributeSourceGate>> {
    public static final AttributeQueryWorkerComparator INSTANCE = new AttributeQueryWorkerComparator();

    @Override
    public int compare(
            AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource, ? extends AttributeSourceGate>, ? extends AttributeSourceGate> o1, 
            AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource, ? extends AttributeSourceGate>, ? extends AttributeSourceGate> o2) {
        return AttributeSourceConfigComparator.INSTANCE.compare(o1.getSourceConfig(), o2.getSourceConfig());
    }
}