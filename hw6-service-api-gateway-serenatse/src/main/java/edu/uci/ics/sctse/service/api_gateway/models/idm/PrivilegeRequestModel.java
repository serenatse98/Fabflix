package edu.uci.ics.sctse.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class PrivilegeRequestModel
    extends RequestModel
{
    private String email;
    private int plevel;

    @JsonCreator
    public PrivilegeRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    @Override
    public String toString() {
        return "PrivilegeRequestModel [Email: " + email + ", plevel: " + plevel + "]";
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "plevel")
    public int getPlevel()
    {
        return plevel;
    }
}
