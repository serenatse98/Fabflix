package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyPrivilegeResponseModel
    extends GeneralResponseModel
{
    @JsonCreator
    public VerifyPrivilegeResponseModel(int resultCode) {
        super(resultCode);
    }
}
