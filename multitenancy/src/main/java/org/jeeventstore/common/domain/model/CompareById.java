package org.jeeventstore.common.domain.model;

/**
 * Represents the ability to compare two objects by identity.
 */
public interface CompareById<T> {

    /**
     * Tests whether this has the same identity as the other object.
     *
     * @param other  the other object to compare with
     * @return  true if the identities are the same, regardless of other attributes.
     */
    boolean sameIdentityAs(T other);
    
}
