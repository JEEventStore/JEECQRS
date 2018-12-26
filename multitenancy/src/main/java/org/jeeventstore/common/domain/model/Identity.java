package org.jeeventstore.common.domain.model;

/**
 * Provides the ability to represent a unique concept in a unique way.
 */
public interface Identity {

    /**
     * Gets a unique String representation of this identity.
     * 
     * @return the String representation
     */
    String idString();

}
