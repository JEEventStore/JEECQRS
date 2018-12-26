package org.jeeventstore.jeecqrs.multitenancy.domain.model;

import org.jeeventstore.common.domain.model.core.AbstractId;

/**
 * Uniquely identifies Tenants.
 *
 * @author langer
 */
public class TenantId extends AbstractId<TenantId> {

    public TenantId(String idString) {
        super(idString);
    }

}
