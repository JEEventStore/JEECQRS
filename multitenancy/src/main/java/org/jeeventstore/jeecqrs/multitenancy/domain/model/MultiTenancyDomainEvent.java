package org.jeeventstore.jeecqrs.multitenancy.domain.model;

import org.jeeventstore.common.domain.model.DomainEvent;

/**
 *
 * @author langer
 */
public interface MultiTenancyDomainEvent<T> extends DomainEvent<T> {

    TenantId tenantId();
    
}
