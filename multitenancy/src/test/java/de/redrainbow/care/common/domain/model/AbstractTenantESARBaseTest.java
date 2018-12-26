package de.redrainbow.care.common.domain.model;

import org.jeeventstore.common.domain.model.core.AbstractTenantESAR;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author alex
 */
public abstract class AbstractTenantESARBaseTest<T extends AbstractTenantESAR>
        extends AbstractEventSourcedAggregateRootBaseTest<T> {

    @Test
    public void test_fresh_has_tenantId() {
        System.out.println("test_fresh_has_tenantId");
        assertNotNull(fresh_instance().tenantId());
    }
    
    @Test
    public void test_replay_has_tenantId() {
        System.out.println("test_replay_has_tenantId");
        T template = fresh_instance();
        T instance = replayed_instance(template);
        assertExpectedForNewInstance(instance);
        assertEquals(instance.tenantId(), template.tenantId());
    }

}