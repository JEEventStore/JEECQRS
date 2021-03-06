/*
 * Copyright (c) 2013 Red Rainbow IT Solutions GmbH, Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jeecqrs.sagas.tracker.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity
@Table(name="saga_tracker")
public class JPASagaTrackerEntry implements Serializable {

    public final static int EVENT_TYPE_LENGTH = 2048;

    /**
     * Internal database ID of the entry.
     */
    @SequenceGenerator(name="saga_tracker_id_seq", sequenceName="saga_tracker_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="saga_tracker_id_seq")
    @Column(name = "id", updatable = false)
    @Id
    private Long id;

    @Column(name= "saga_id")
    @NotNull
    private String sagaId;

    private String description;

    @Column(name = "event_type", length = EVENT_TYPE_LENGTH)
    @NotNull
    private String eventType;

    /*
     * We cannot use @Lob, because it will cause Hibernate to map the field
     * in ascii-only mode when used with PostreSQL, which leads to broken
     * UTF8 Strings.  Both parties refuse to fix this isse, see, e.g.,
     * https://groups.google.com/forum/#!topic/pgsql.interfaces.jdbc/g4XXAL-a5tE
     * http://in.relation.to/15492.lace
     * https://hibernate.atlassian.net/browse/HHH-6127
     *
     * We therefore fix the length at 32,672 , which is the maximum VARCHAR
     * size in Apache Derby, the database used for unit tests.  In production
     * mode you are expected to manually create the table with the correct
     * length and field types.
     */
    @Column(name = "event_body", length = 32672)
    @NotNull
    private String eventBody;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date timeout;

    public JPASagaTrackerEntry(String sagaId, String description,
            String eventType, String eventBody, Date timeout) {
        this.sagaId = sagaId;
        this.description = description;
	this.eventType = eventType;
	this.eventBody = eventBody;
	this.timeout = new Date(timeout.getTime());
    }

    public Long id() {
        return id;
    }

    public String sagaId() {
        return sagaId;
    }

    public String description() {
        return description;
    }

    public String eventType() {
	return eventType;
    }

    public String eventBody() {
	return eventBody;
    }

    public Date timeout() {
	return new Date(timeout.getTime());
    }

    // required for JPA
    @Deprecated
    protected JPASagaTrackerEntry() { }

}
