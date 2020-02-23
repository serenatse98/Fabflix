package edu.uci.ics.sctse.service.basic.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "valid" })
public interface Validate {
    boolean isValid();
}
