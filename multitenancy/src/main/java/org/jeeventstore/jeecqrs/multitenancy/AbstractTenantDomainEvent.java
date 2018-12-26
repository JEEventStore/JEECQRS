package org.jeeventstore.jeecqrs.multitenancy;

import org.jeeventstore.jeecqrs.multitenancy.domain.model.TenantId;
import org.jeeventstore.jeecqrs.multitenancy.domain.model.MultiTenancyDomainEvent;
import org.jeeventstore.common.domain.model.core.AbstractEvent;

/**
 *
 * @author langer
 */
public abstract class AbstractTenantDomainEvent<T> 
	extends AbstractEvent<T>
	implements MultiTenancyDomainEvent<T> {

    private TenantId tenantId;

    public AbstractTenantDomainEvent(TenantId tenantId) {
	this.tenantId = tenantId;
    }

    @Override
    public final TenantId tenantId() {
	return tenantId;
    }

}
