package com.bootlabs.springbatch5mongodb.batch.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(useDiscriminator = false, value = "Sequence")
public class SequenceGenerator {

    @Id
    protected String type;

    protected Long value = 1L;

    protected SequenceGenerator() {
        super();
    }

    public SequenceGenerator(final String type) {
        this.type = type;
    }

    public SequenceGenerator(final String type, final Long startValue) {
        this(type);
        value = startValue;
    }

    public Long getValue() {
        return value;
    }
}
