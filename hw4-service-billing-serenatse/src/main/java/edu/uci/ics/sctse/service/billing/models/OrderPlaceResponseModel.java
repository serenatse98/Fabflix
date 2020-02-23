package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPlaceResponseModel
    extends GeneralResponseModel
{
    @JsonProperty(value = "redirectURL")
    private String redirectURL;
    @JsonProperty(value = "token")
    private String token;

    @JsonCreator
    public OrderPlaceResponseModel() { }

    @JsonCreator
    public OrderPlaceResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "redirectURL") String redirectURL,
            @JsonProperty(value = "token") String token)
    {
        super(resultCode, message);
        this.redirectURL = redirectURL;
        this.token = token;
    }

    @JsonProperty(value = "redirectURL")
    public String getRedirectURL()
    {
        return redirectURL;
    }

    @JsonProperty(value = "token")
    public String getToken()
    {
        return token;
    }
}
