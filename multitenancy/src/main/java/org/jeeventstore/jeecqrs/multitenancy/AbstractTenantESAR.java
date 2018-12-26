package org.jeeventstore.jeecqrs.multitenancy;

import org.jeeventstore.jeecqrs.multitenancy.domain.model.TenantId;
import org.jeeventstore.common.domain.model.core.AbstractEventSourcedAggregateRoot;
import org.apache.commons.lang.Validate;

/**
 * Layered super type (see P of EAA) for ARs that are to be separated
 * by tenant.
 * It always consists of a TenantId.
 * 
 * @author langer
 */
public abstract class AbstractTenantESAR<T>
	extends AbstractEventSourcedAggregateRoot<T> {

    private TenantId tenantId;

    /**
     * ID of the tenant associated to this entry
     * @return 
     */
    public final TenantId tenantId() {
	return tenantId;
    }

    protected final void setTenantId(TenantId tenantId) {
	Validate.notNull(tenantId, "tenant must not be null");
	this.tenantId = tenantId;
    }

}
