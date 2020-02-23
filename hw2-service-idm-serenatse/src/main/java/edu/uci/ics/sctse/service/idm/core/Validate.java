package edu.uci.ics.sctse.service.idm.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "valid" })
public interface Validate {
    boolean isValid();
}